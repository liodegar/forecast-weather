package com.finleap.restapp.forecastweather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

/**
 * Created by Liodegar on 10/26/2018.
 */
public abstract class AbstractSourceProvider implements SourceProvider {

    public static final int START_DAILY_HOUR = 6;
    public static final int END_DAILY_HOUR = 18;
    public static final String DEFAULT_PRESSURE_UNIT = "hPa";
    public static final int MIDNIGHT = 0;
    public static final int HIGHEST_HOUR_DAY = 23;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected Environment env;

    protected LocalDate getNowPlusThreeDays() {
        LocalDate localDate = LocalDate.now();
        return localDate.plusDays(3);
    }


}
