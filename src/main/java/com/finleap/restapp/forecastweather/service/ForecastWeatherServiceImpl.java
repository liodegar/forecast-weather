package com.finleap.restapp.forecastweather.service;

import com.finleap.restapp.forecastweather.model.City;
import com.finleap.restapp.forecastweather.model.ForecastWeatherMetrics;
import com.finleap.restapp.forecastweather.model.Magnitude;
import com.finleap.restapp.forecastweather.model.TemperatureEnum;
import com.finleap.restapp.forecastweather.model.WeatherData;
import com.finleap.restapp.forecastweather.repository.dao.ForecastWeatherDao;
import com.finleap.restapp.forecastweather.service.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Predicate;


/**
 * Created by Liodegar.
 */
@Service
public class ForecastWeatherServiceImpl implements ForecastWeatherService {

    private static final Logger logger = LoggerFactory.getLogger(ForecastWeatherServiceImpl.class);

    public static final int START_DAILY_HOUR = 6;
    public static final int END_DAILY_HOUR = 18;
    public static final String DEFAULT_PRESSURE_UNIT = "hPa";
    public static final int MIDNIGHT = 0;
    public static final int HIGHEST_HOUR_DAY = 23;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ForecastWeatherIntegrationService forecastWeatherIntegrationService;

    @Autowired
    ForecastWeatherDao forecastWeatherDao;


    /**
     * Gets the forecast weather metrics for the given city
     *
     * @param city a City instance
     * @return a ForecastWeatherMetrics instance containing the required averages
     * @throws BusinessException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    @Override
    public ForecastWeatherMetrics getForecastWeatherMetrics(City city) throws BusinessException {
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

    protected LocalDate getNowPlusThreeDays() {
        LocalDate localDate = LocalDate.now();
        return localDate.plusDays(3);
    }

    protected Magnitude getTemperatureAverageMagnitude(WeatherData weatherData, Predicate<WeatherData.Forecast.Time> predicate,
                                                       Predicate<WeatherData.Forecast.Time> limitDays) {
        double dailyTemperatureAverage = weatherData.getForecast().getTime()
                .stream()
                .filter(Objects::nonNull)
                .filter(limitDays)
                .filter(predicate)
                .peek(time -> logger.info("from=" + time.getFrom() + ", to=" + time.getTo() + ", temp=" + time.getTemperature().getValue()))
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
     * Retrieves the WeatherData by using the cityId
     *
     * @param city a City instance containing cityId
     * @return a WeatherData instance
     */
    private WeatherData getForecastWeatherMetricsByCityId(City city) {
        return forecastWeatherIntegrationService.getForecastWeatherMetricsByCityId(city);
    }
}
