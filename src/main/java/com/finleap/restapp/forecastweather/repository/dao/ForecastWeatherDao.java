package com.finleap.restapp.forecastweather.repository.dao;

import com.finleap.restapp.forecastweather.model.City;
import com.finleap.restapp.forecastweather.service.exception.IntegrationException;

import java.util.List;

/**
 * Created by Liodegar on 10/14/2018.
 */
public interface ForecastWeatherDao {

    /**
     * Retrieves all the cities from the underlying repository
     *
     * @return a List of Cities instance
     * @throws IntegrationException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    List<City> findAllCities() throws IntegrationException;

    /**
     * Retrieves a city by id
     *
     * @param id The cityId
     * @return an instance of City
     * @throws IntegrationException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    City findCityById(Long id) throws IntegrationException;

    /**
     * Retrieves a city by city name and country ISO code
     *
     * @param city A city containing the name and the country ISO code
     * @return @return an instance of City
     * @throws IntegrationException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    City findCityByNameAndCountryCode(City city) throws IntegrationException;
}
