package com.finleap.restapp.forecastweather.controller;

import com.finleap.restapp.forecastweather.aop.TokenRequired;
import com.finleap.restapp.forecastweather.controller.dto.*;
import com.finleap.restapp.forecastweather.controller.exception.ExceptionHandler;
import com.finleap.restapp.forecastweather.model.City;
import com.finleap.restapp.forecastweather.model.ForecastWeatherMetrics;
import com.finleap.restapp.forecastweather.service.ForecastWeatherService;
import com.finleap.restapp.forecastweather.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller represents the entry point to the Forecast Weather API
 * Created by Liodegar.
 */
@RestController
@RequestMapping("/weather")
public class ForecastWeatherController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ForecastWeatherController.class);

    @Autowired
    private ExceptionHandler exceptionHandler;

    @Autowired
    private ForecastWeatherService forecastWeatherService;

    @Autowired
    private SecurityService securityService;

    /**
     * Generates the token for the given user
     *
     * @param serviceRequest An instance of ServiceRequest<String> containing the subject
     * @return a ServiceResponse<String>> containing the generated token
     */
    @RequestMapping("/generateToken")
    public ServiceResponse<String> generateToken(@RequestBody ServiceRequest<String> serviceRequest) {
        ServiceResponse<String> serviceResponse = new ServiceResponse<>();
        try {
            checkParameter(serviceRequest);
            long threeMinutes = 3 * 1000 * 60;
            String token = securityService.generateToken(serviceRequest.getParameter(), threeMinutes);
            handleResponse(serviceResponse, token);
        } catch (Exception e) {
            exceptionHandler.handleControllerException(serviceResponse, serviceRequest, e, getMethodName());
        }
        return serviceResponse;
    }


    /**
     * Generates the token for the given user
     *
     * @param serviceRequest An instance of ServiceRequest<City> containing a City instance
     * @return a ServiceResponse<String>> containing the generated token
     */
    @RequestMapping("/data")
    @TokenRequired
    public ServiceResponse<ForecastWeatherMetrics> getForecastWeatherMetric(@RequestBody ServiceRequest<City> serviceRequest) {
        ServiceResponse<ForecastWeatherMetrics> serviceResponse = new ServiceResponse<>();
        try {
            checkCityParameter(serviceRequest);
            ForecastWeatherMetrics forecastWeatherMetric = forecastWeatherService.getForecastWeatherMetrics(serviceRequest.getParameter());
            serviceResponse.setCode(forecastWeatherMetric.isValid() ? ResponseCodeEnum.SUCCESS : ResponseCodeEnum.UNSUCCESS);
            serviceResponse.setResult(forecastWeatherMetric);

        } catch (Exception e) {
            exceptionHandler.handleControllerException(serviceResponse, serviceRequest, e, getMethodName());
        }
        return serviceResponse;
    }
}