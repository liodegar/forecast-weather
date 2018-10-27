package com.finleap.restapp.forecastweather.service;

import com.finleap.restapp.forecastweather.model.City;
import com.finleap.restapp.forecastweather.model.ForecastWeatherMetrics;
import com.finleap.restapp.forecastweather.model.Magnitude;
import com.finleap.restapp.forecastweather.model.TemperatureEnum;
import com.finleap.restapp.forecastweather.service.dto.MagnitudeInfo;
import com.finleap.restapp.forecastweather.service.dto.MockyInfo;
import com.finleap.restapp.forecastweather.service.exception.IntegrationException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * SourceProvider implementation to get the info from SourceProvider
 * Created by Liodegar on 10/26/2018.
 */
@Service("com.finleap.restapp.forecastweather.service.MockySourceProviderImpl")
public class MockySourceProviderImpl extends AbstractSourceProvider {

    private static final Logger logger = LoggerFactory.getLogger(MockySourceProviderImpl.class);

    @Value("${mocky.endpoint}")
    private String mockyEndPoint;

    @Override
    public ForecastWeatherMetrics getForecastWeatherMetrics(City city) {
        MockyInfo mockyInfo = getMockyInfoByCityId(city);
        return getForecastWeatherMetrics(mockyInfo, city);
    }

    private ForecastWeatherMetrics getForecastWeatherMetrics(MockyInfo mocky, City city) {

        ForecastWeatherMetrics forecastWeatherMetrics = new ForecastWeatherMetrics(city);
        //Circuit breaker
        if (CollectionUtils.isEmpty(mocky.getList())) {
            return forecastWeatherMetrics;
        }

        LocalDate nowPlusThreeDays = getNowPlusThreeDays();
        Predicate<MagnitudeInfo> limitDays = magnitudeInfo -> (magnitudeInfo.getDatetime().toLocalDate().isBefore(nowPlusThreeDays));

        //Calculating the dailyTemperatureAverageMagnitude
        Predicate<MagnitudeInfo> dailyPredicate = magnitudeInfo -> (magnitudeInfo.getDatetime().getHour() >= START_DAILY_HOUR
                && magnitudeInfo.getDatetime().getHour() <= END_DAILY_HOUR);

        logger.info("ForecastWeatherServiceImpl.day");
        Magnitude dailyTemperatureAverageMagnitude = getTemperatureAverageMagnitude(mocky, limitDays, dailyPredicate);
        forecastWeatherMetrics.setDailyTemperatureAverage(dailyTemperatureAverageMagnitude);

        //Calculating the nightlyTemperatureAverageMagnitude
        Predicate<MagnitudeInfo> nightlyPredicate = dailyPredicate.negate();

        logger.info("ForecastWeatherServiceImpl.night");
        Magnitude nightlyTemperatureAverageMagnitude = getTemperatureAverageMagnitude(mocky, limitDays, nightlyPredicate);
        forecastWeatherMetrics.setNightlyTemperatureAverage(nightlyTemperatureAverageMagnitude);

        //Calculating the pressureAverage
        forecastWeatherMetrics.setPressureAverage(getPressureAverageMagnitude(mocky, limitDays));

        return forecastWeatherMetrics;
    }

    protected Magnitude getTemperatureAverageMagnitude(MockyInfo mockyInfo, Predicate<MagnitudeInfo> predicate,
                                                       Predicate<MagnitudeInfo> limitDays) {
        double dailyTemperatureAverage = mockyInfo.getList()
                .stream()
                .filter(Objects::nonNull)
                .filter(limitDays)
                .filter(predicate)
                .peek(magnitudeInfo -> logger.info("dateTime=" + magnitudeInfo.getDatetime()))
                .map(magnitudeInfo -> magnitudeInfo.getData())
                .map(data -> data.getTemperature())
                .mapToDouble(value -> value.doubleValue())
                .average()
                .orElse(0L);

        return new Magnitude(TemperatureEnum.FAHRENHEIT.getCode(), dailyTemperatureAverage);
    }

    protected Magnitude getPressureAverageMagnitude(MockyInfo mockyInfo, Predicate<MagnitudeInfo> limitDays) {
        double dailyTemperatureAverage = mockyInfo.getList()
                .stream()
                .filter(Objects::nonNull)
                .filter(limitDays)
                .map(magnitudeInfo -> magnitudeInfo.getData())
                .map(data -> data.getPressure())
                .mapToDouble(value -> value.doubleValue())
                .average()
                .orElse(0L);

        return new Magnitude(DEFAULT_PRESSURE_UNIT, dailyTemperatureAverage);
    }


    @HystrixCommand(fallbackMethod = "defaultMockyInfo")
    public MockyInfo getMockyInfoByCityId(City city) throws IntegrationException {
        try {
            return restTemplate.getForObject(getMockyUrlByCityId(city), MockyInfo.class);
        } catch (Exception e) {
            throw new IntegrationException("Exception encountered invoking getMockyInfoByCityId with param=" + city, e);
        }
    }

    /**
     * This is default MockyInfo instance used in case of connection failure with the Mocky weather API provider
     *
     * @param city an instance of City containing its id
     * @return a MockyInfo instance
     */
    @HystrixCommand
    private MockyInfo defaultMockyInfo(City city) {
        return new MockyInfo();
    }

    private String getMockyUrlByCityId(City city) {
        StringBuilder builder = new StringBuilder(150);
        builder.append(mockyEndPoint).append(city.getId());
        return builder.toString();
    }

}
