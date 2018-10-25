package com.finleap.restapp.forecastweather.service;

import com.finleap.restapp.forecastweather.model.City;
import com.finleap.restapp.forecastweather.model.ForecastWeatherMetrics;
import com.finleap.restapp.forecastweather.service.exception.BusinessException;

/**
 * This service exposes the core forecast weather functionalities
 * Created by Liodegar.
 */
public interface ForecastWeatherService {



    /**
     * Gets the forecast weather metrics for the given city
     * @param city a City instance
     * @return a ForecastWeatherMetrics instance containing the required averages
     * @throws BusinessException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    ForecastWeatherMetrics getForecastWeatherMetrics(City city) throws BusinessException;
}
