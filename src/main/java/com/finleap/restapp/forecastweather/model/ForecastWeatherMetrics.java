package com.finleap.restapp.forecastweather.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by Liodegar on 10/13/2018.
 */
public class ForecastWeatherMetrics implements Serializable, Integrity {

    private static final long serialVersionUID = 3230403280527814469L;

    private City city;

    /**
     * Average of daily (6h-18h) temperature, in Celsius, for the following 3 days
     */
    private Magnitude dailyTemperatureAverage;

    /**
     * Average of nightly (18h-6h) temperature, in Celsius, for the following 3 days
     */
    private Magnitude nightlyTemperatureAverage;

    /**
     * Average of pressure for the following 3 days
     */
    private Magnitude pressureAverage;

    public ForecastWeatherMetrics(City city) {
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    public Magnitude getDailyTemperatureAverage() {
        return dailyTemperatureAverage;
    }

    public Magnitude getNightlyTemperatureAverage() {
        return nightlyTemperatureAverage;
    }

    public Magnitude getPressureAverage() {
        return pressureAverage;
    }

    public void setDailyTemperatureAverage(Magnitude dailyTemperatureAverage) {
        this.dailyTemperatureAverage = dailyTemperatureAverage;
    }

    public void setNightlyTemperatureAverage(Magnitude nightlyTemperatureAverage) {
        this.nightlyTemperatureAverage = nightlyTemperatureAverage;
    }

    public void setPressureAverage(Magnitude pressureAverage) {
        this.pressureAverage = pressureAverage;
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return dailyTemperatureAverage != null && nightlyTemperatureAverage != null && pressureAverage != null ;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ForecastWeatherMetrics{");
        sb.append("city=").append(city);
        sb.append(", dailyTemperatureAverage=").append(dailyTemperatureAverage);
        sb.append(", nightlyTemperatureAverage=").append(nightlyTemperatureAverage);
        sb.append(", pressureAverage=").append(pressureAverage);
        sb.append('}');
        return sb.toString();
    }
}
