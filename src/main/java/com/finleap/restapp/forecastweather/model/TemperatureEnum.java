package com.finleap.restapp.forecastweather.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.EnumSerializer;
import com.finleap.restapp.forecastweather.utils.EnumConverter;
import com.finleap.restapp.forecastweather.utils.ReverseEnumMap;

/**
 * Created by Liodegar on 10/14/2018.
 */
@JsonSerialize(using = EnumSerializer.class)
public enum TemperatureEnum implements EnumConverter<TemperatureEnum> {
    CELSIUS(1, "Celsius"),
    KELVIN(2, "Kelvin"),
    FAHRENHEIT(3, "Fahrenheit");

    private static ReverseEnumMap<TemperatureEnum> map = new ReverseEnumMap<>(TemperatureEnum.class);
    private final byte id;
    private final String code;

    TemperatureEnum(int id, String code) {
        this.id = (byte) id;
        this.code = code;
    }

    @Override
    public byte getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }


    @Override
    public TemperatureEnum convert(byte val) {
        return getFromValue(val);
    }

    @Override
    public TemperatureEnum convert(String code) {
        return getFromCode(code);
    }


    @JsonCreator
    public static TemperatureEnum getFromValue(int val) {
        return map.get((byte) val);
    }

    @JsonCreator
    public static TemperatureEnum getFromCode(String code) {
        return map.get(code);
    }

}
