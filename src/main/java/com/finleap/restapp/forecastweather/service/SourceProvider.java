package com.finleap.restapp.forecastweather.service;

import com.finleap.restapp.forecastweather.model.City;
import com.finleap.restapp.forecastweather.model.ForecastWeatherMetrics;

/**
 * Defines the contract to get ForecastWeatherMetrics from different providers
 * Created by Liodegar on 10/26/2018.
 */
public interface SourceProvider {

    /**
     * Gets the ForecastWeatherMetrics from a specific provider
     * @param city The city parameter
     * @return a ForecastWeatherMetrics instance
     */
    ForecastWeatherMetrics getForecastWeatherMetrics(City city);
}
