package com.finleap.restapp.forecastweather.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Generic DTO instance to be used as response from every service
 */
public class ServiceResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Represents the status code for the operation result allowing to determine if was succesfully executed or not
     */
    private ResponseCodeEnum code;

    /**
     * Contains the result wrapped by the current instance
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;
    /**
     * Contains the error details for the operation. This is only set in case of errors
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ServiceError error;


    public ServiceResponse() {
        this(ResponseCodeEnum.UNSUCCESS);
    }

    public ServiceResponse(ResponseCodeEnum code) {
        if (code == null) {
            code = ResponseCodeEnum.UNSUCCESS;
        }
        this.code = code;
    }

    public ServiceResponse(ResponseCodeEnum code, T result) {
        this(code);
        this.result = result;
    }

    public ResponseCodeEnum getCode() {
        return this.code;
    }

    public void setCode(ResponseCodeEnum code) {
        this.code = code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public ServiceError getError() {
        return error;
    }

    public void setError(ServiceError serviceError) {
        this.error = serviceError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceResponse<?> that = (ServiceResponse<?>) o;
        if (code != that.code) return false;
        return !(result != null ? !result.equals(that.result) : that.result != null);
    }

    @Override
    public int hashCode() {
        int result1 = code != null ? code.hashCode() : 0;
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        return result1;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ServiceResponse{");
        sb.append("code=").append(code.toString());
        sb.append(", result=").append(result != null ? result.toString() : "null");
        sb.append(", error=").append(error != null ? error.toString() : "null");
        sb.append('}');
        return sb.toString();
    }
}