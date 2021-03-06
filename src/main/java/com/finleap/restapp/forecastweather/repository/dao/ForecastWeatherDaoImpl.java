package com.finleap.restapp.forecastweather.repository.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finleap.restapp.forecastweather.model.City;
import com.finleap.restapp.forecastweather.service.exception.IntegrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Liodegar on 10/14/2018.
 */
@Repository
public class ForecastWeatherDaoImpl implements ForecastWeatherDao {

    @Autowired
    ResourceLoader resourceLoader;

    /**
     * Retrieves all the cities from the underlying repository
     *
     * @return a List of Cities instances
     * @throws IntegrationException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    @Override
    @Cacheable(value = "allACitiesCache", unless = "#result.size() == 0")
    public List<City> findAllCities() throws IntegrationException {
        try {
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return objectMapper.readValue(getCityResource().getInputStream(), new TypeReference<ArrayList<City>>() {
            });

        } catch (Exception e) {
            throw new IntegrationException("Exception encountered invoking findAllCities", e);
        }
    }

    /**
     * Retrieves a city by id
     *
     * @param cityId The cityId
     * @return an instance of City
     * @throws IntegrationException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    @Override
    @Cacheable(value = "cityCache", key = "#cityId")
    public City findCityById(Long cityId) throws IntegrationException {
        return findAllCities().stream()
                .filter(Objects::nonNull)
                .filter(city -> cityId.equals(city.getId())).findFirst().orElse(null);
    }

    /**
     * Retrieves a city by city name and country ISO code
     *
     * @param cityParam A city containing the name and the country ISO code
     * @return @return an instance of City
     * @throws IntegrationException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    @Override
    public City findCityByNameAndCountryCode(City cityParam) throws IntegrationException {
        List<City> cities = findAllCities();
        City retrievedCity = cities.stream()
                .filter(Objects::nonNull)
                .filter(city -> cityParam.getName().equals(city.getName()))
                .filter(city -> cityParam.getIsoCountryCode().equals(city.getIsoCountryCode()))
                .findFirst().orElse(null);
        cityParam.setId(retrievedCity == null ? null : retrievedCity.getId());
        return retrievedCity;
    }

    /**
     * The resource with the city info from a file given by the API provider
     * @return a Resource instance
     */
    private Resource getCityResource() {
        return resourceLoader.getResource("classpath:static/city.list.json");
    }

}
