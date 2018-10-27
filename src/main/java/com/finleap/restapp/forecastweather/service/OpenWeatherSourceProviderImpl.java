package com.finleap.restapp.forecastweather.service;

import com.finleap.restapp.forecastweather.model.City;
import com.finleap.restapp.forecastweather.model.ForecastWeatherMetrics;
import com.finleap.restapp.forecastweather.model.Magnitude;
import com.finleap.restapp.forecastweather.model.TemperatureEnum;
import com.finleap.restapp.forecastweather.repository.dao.ForecastWeatherDao;
import com.finleap.restapp.forecastweather.service.dto.WeatherData;
import com.finleap.restapp.forecastweather.service.exception.BusinessException;
import com.finleap.restapp.forecastweather.service.exception.IntegrationException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * SourceProvider implementation to get the info from Open Weather API
 * Created by Liodegar on 10/26/2018.
 */
@Service("com.finleap.restapp.forecastweather.service.OpenWeatherSourceProviderImpl")
public class OpenWeatherSourceProviderImpl extends AbstractSourceProvider {

    private static final Logger logger = LoggerFactory.getLogger(OpenWeatherSourceProviderImpl.class);

    @Autowired
    private ForecastWeatherDao forecastWeatherDao;

    @Value("${openweathermap.endpointByCityID}")
    private String endPoint;

    @Value("${openweathermap.appKey}")
    private String appKey;

    /**
     * Gets the ForecastWeatherMetrics from a specific provider
     *
     * @param city The city parameter
     * @return a ForecastWeatherMetrics instance
     */
    @Override
    public ForecastWeatherMetrics getForecastWeatherMetrics(City city) {
        try {
            WeatherData weatherData = getWeatherData(city);
            ForecastWeatherMetrics forecastWeatherMetrics = new ForecastWeatherMetrics(city);
            //Circuit breaker
            if (weatherData.getForecast() == null) {
                return forecastWeatherMetrics;
            }

            LocalDate nowPlusThreeDays = getNowPlusThreeDays();
            Predicate<WeatherData.Forecast.Time> limitDays = time -> (time.getFrom().toLocalDate().isBefore(nowPlusThreeDays));

            //Calculating the dailyTemperatureAverageMagnitude
            Predicate<WeatherData.Forecast.Time> dailyPredicate = time -> (time.getFrom().getHour() >= START_DAILY_HOUR
                    && time.getFrom().getHour() < END_DAILY_HOUR
                    && time.getTo().getHour() <= END_DAILY_HOUR
                    && time.getTo().getHour() > START_DAILY_HOUR);

            logger.info("ForecastWeatherServiceImpl.day");
            Magnitude dailyTemperatureAverageMagnitude = getTemperatureAverageMagnitude(weatherData, limitDays, dailyPredicate);
            forecastWeatherMetrics.setDailyTemperatureAverage(dailyTemperatureAverageMagnitude);

            //Calculating the nightlyTemperatureAverageMagnitude
            Predicate<WeatherData.Forecast.Time> nightlyPredicate = time -> (
                    (time.getFrom().getHour() >= MIDNIGHT
                            && time.getTo().getHour() <= START_DAILY_HOUR)
                            || (time.getFrom().getHour() >= END_DAILY_HOUR
                            && time.getTo().getHour() <= HIGHEST_HOUR_DAY));

            logger.info("ForecastWeatherServiceImpl.night");
            Magnitude nightlyTemperatureAverageMagnitude = getTemperatureAverageMagnitude(weatherData, limitDays, nightlyPredicate);
            forecastWeatherMetrics.setNightlyTemperatureAverage(nightlyTemperatureAverageMagnitude);

            //Calculating the pressureAverage
            forecastWeatherMetrics.setPressureAverage(getPressureAverageMagnitude(weatherData, limitDays));

            return forecastWeatherMetrics;
        } catch (Exception e) {
            throw new BusinessException("Exception encountered invoking getForecastWeatherMetrics with param=" + city, e);
        }
    }

    protected Magnitude getTemperatureAverageMagnitude(WeatherData weatherData, Predicate<WeatherData.Forecast.Time> predicate,
                                                       Predicate<WeatherData.Forecast.Time> limitDays) {
        double dailyTemperatureAverage = weatherData.getForecast().getTime()
                .stream()
                .filter(Objects::nonNull)
                .filter(limitDays)
                .filter(predicate)
                .peek(time -> logger.info("from=" + time.getFrom() + ", to=" + time.getTo() + ", temperature=" + time.getTemperature().getValue()))
                .map(time -> time.getTemperature())
                .map(temp -> temp.getValue())
                .mapToDouble(value -> value.doubleValue())
                .average()
                .orElse(0L);

        return new Magnitude(TemperatureEnum.CELSIUS.getCode(), dailyTemperatureAverage);
    }

    protected Magnitude getPressureAverageMagnitude(WeatherData weatherData, Predicate<WeatherData.Forecast.Time> limitDays) {
        double dailyTemperatureAverage = weatherData.getForecast().getTime()
                .stream()
                .filter(Objects::nonNull)
                .filter(limitDays)
                .map(time -> time.getPressure())
                .map(temp -> temp.getValue())
                .mapToDouble(value -> value.doubleValue())
                .average()
                .orElse(0L);

        String pressureUnit = weatherData.getForecast().getTime()
                .stream()
                .filter(Objects::nonNull)
                .map(time -> time.getPressure())
                .map(temp -> temp.getUnit())
                .findFirst().orElse(DEFAULT_PRESSURE_UNIT);

        return new Magnitude(pressureUnit, dailyTemperatureAverage);
    }

    private WeatherData getWeatherData(City city) {
        if (city.getId() != null) {
            return getForecastWeatherMetricsByCityId(city);
        } else {
            return getForecastWeatherMetricsByCityId(getForecastWeatherMetricsByCityName(city));
        }
    }

    /**
     * Retrieves the city from the cached repository
     *
     * @param city a City instance containing name and isoCountryCode
     * @return a City instance
     */
    private City getForecastWeatherMetricsByCityName(City city) {
        return forecastWeatherDao.findCityByNameAndCountryCode(city);
    }

    /**
     * Gets the WeatherData from the external weather provider.
     * This functionality implements the Circuit Breaker pattern to avoid affecting the client
     *
     * @param city an instance of City containing its id
     * @return a WeatherData instance
     * @throws IntegrationException
     */
    @HystrixCommand(fallbackMethod = "defaultWeatherData")
    public WeatherData getForecastWeatherMetricsByCityId(City city) throws IntegrationException {
        try {
            return restTemplate.getForObject(getUrlByCityId(city), WeatherData.class);

        } catch (Exception e) {
            throw new IntegrationException("Exception encountered invoking getForecastWeatherMetricsByCityName with param=" + city, e);
        }
    }


    /**
     * This is default WeatherData instance used in case of connection failure with the weather API provider
     *
     * @param city an instance of City containing its id
     * @return a WeatherData instance
     */
    @HystrixCommand
    private WeatherData defaultWeatherData(City city) {
        return new WeatherData();
    }

    private String getUrlByCityId(City city) {
        StringBuilder builder = new StringBuilder(150);
        builder.append(endPoint).append(city.getId()).append("&APPID=").append(appKey);
        return builder.toString();
    }
}
