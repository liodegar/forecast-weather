package com.finleap.restapp.forecastweather.service;

import com.finleap.restapp.forecastweather.model.City;
import com.finleap.restapp.forecastweather.model.WeatherData;
import com.finleap.restapp.forecastweather.service.exception.IntegrationException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Liodegar on 10/13/2018.
 */
@Service
public class ForecastWeatherIntegrationServiceImpl implements ForecastWeatherIntegrationService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment env;

    /**
     * Gets the WeatherData from the external weather provider.
     * This functionality implements the Circuit Breaker pattern to avoid affecting the client
     * @param city an instance of City containing its id
     * @return a WeatherData instance
     * @throws IntegrationException
     */
    @HystrixCommand(fallbackMethod = "defaultWeatherData")
    @Override
    public WeatherData getForecastWeatherMetricsByCityId(City city) throws IntegrationException {
        try {
            return restTemplate.getForObject(getUrlByCityId(city), WeatherData.class);

        } catch (Exception e) {
            throw new IntegrationException("Exception encountered invoking getForecastWeatherMetricsByCityName with param=" + city, e);
        }
    }

    /**
     * This is default WeatherData instance used in case of connection failure with the weather API provider
     * @param city an instance of City containing its id
     * @return a WeatherData instance
     */
    @HystrixCommand
    private WeatherData defaultWeatherData(City city) {
        return new WeatherData();
    }

    private String getUrlByCityId(City city) {
        String appKey = env.getProperty("openweathermap.appKey");
        String endPoint = env.getProperty("openweathermap.endpointByCityID");
        StringBuilder builder = new StringBuilder(150);
        builder.append(endPoint).append(city.getId()).append("&APPID=").append(appKey);
        return builder.toString();
    }
}
