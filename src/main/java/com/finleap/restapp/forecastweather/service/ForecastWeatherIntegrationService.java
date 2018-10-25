package com.finleap.restapp.forecastweather.service;

import com.finleap.restapp.forecastweather.model.City;
import com.finleap.restapp.forecastweather.model.WeatherData;
import com.finleap.restapp.forecastweather.service.exception.IntegrationException;

/**
 * Created by Liodegar on 10/13/2018.
 */
public interface ForecastWeatherIntegrationService {

    /**
     * Gets the WeatherData from the external weather provider.
     * This functionality implements the Circuit Breaker pattern to avoid affecting the client
     * @param city an instance of City containing its id
     * @return a WeatherData instance
     * @throws IntegrationException
     */
    WeatherData getForecastWeatherMetricsByCityId(City city) throws IntegrationException;
}
