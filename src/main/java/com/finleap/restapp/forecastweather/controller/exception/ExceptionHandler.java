package com.finleap.restapp.forecastweather.controller.exception;

import com.finleap.restapp.forecastweather.controller.dto.*;
import com.finleap.restapp.forecastweather.service.exception.AuthorizationException;
import com.finleap.restapp.forecastweather.service.exception.BusinessException;
import com.finleap.restapp.forecastweather.service.exception.IntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Class for handling exception thrown by the controller
 * Created by Liodegar.
 */
@Component
public class ExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageSource messageSource;

    /**
     *
     * @param serviceResponse Generic DTO instance to be used as response for the controller
     * @param serviceRequest An instance of ServiceRequest<T> containing the actual object as parameter
     * @param e The actual exception encountered
     * @param method The actual method where the exception was encountered
     */
    public <T, E> void handleControllerException(ServiceResponse<T> serviceResponse, ServiceRequest<E> serviceRequest, Exception e, String method) {
        ResponseCodeEnum code = ResponseCodeEnum.ERROR;
        String message = null;
        try {

            if (e instanceof IllegalArgumentException) {
                message = getLocaleMessage(ErrorCodeConstants.ILLEGAL_ARG_EXCEPTION, new Object[]{method});

            } else if (e instanceof BusinessException) {
                message = getLocaleMessage(ErrorCodeConstants.BUSINESS_EXCEPTION, new Object[]{serviceRequest, method});

            } else if (e instanceof AuthorizationException) {
                message = getLocaleMessage(ErrorCodeConstants.AUTHORIZATION_EXCEPTION, new Object[]{serviceRequest, method});

            } else if (e instanceof IntegrationException) {
                message = getLocaleMessage(ErrorCodeConstants.INTEGRATION_EXCEPTION, new Object[]{serviceRequest, method});

            } else if (e instanceof InvalidTokenException) {
                message = getLocaleMessage(ErrorCodeConstants.INVALID_TOKEN_EXCEPTION, new Object[]{serviceRequest, method});
            } else {
                message = getLocaleMessage(ErrorCodeConstants.UNCATEGORIZED_EXCEPTION, new Object[]{serviceRequest, method});
            }
            logAndHandleError(serviceResponse, e, code, message);
        } catch (Exception unknownException) {
            message = getLocaleMessage(ErrorCodeConstants.UNHANDLED_EXCEPTION, new Object[]{unknownException.getClass().getName(), serviceRequest, method});
            logAndHandleError(serviceResponse, e, code, message);
        }
    }

    private String getLocaleMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, Locale.US);
    }

    private void logAndHandleError(ServiceResponse serviceResponse, Exception e, ResponseCodeEnum code, String message) {
        ServiceError serviceError = new ServiceError(message, e);
        serviceResponse.setCode(code);
        serviceResponse.setError(serviceError);
        logger.error(String.valueOf(serviceError), e);
    }


}
