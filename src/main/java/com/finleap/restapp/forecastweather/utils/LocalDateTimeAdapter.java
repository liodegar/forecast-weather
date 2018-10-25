package com.finleap.restapp.forecastweather.utils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;

/**
 * Created by Liodegar on 10/13/2018.
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public LocalDateTime unmarshal(String inputDate) throws Exception {
        return LocalDateTime.parse(inputDate);
    }

    @Override
    public String marshal(LocalDateTime inputDate) throws Exception {
        return inputDate.toString();
    }
}
