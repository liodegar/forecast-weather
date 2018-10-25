package com.finleap.restapp.forecastweather.model;

import java.io.Serializable;

/**
 * Immutable class to model a mathematical magnitude
 * Created by Liodegar on 10/14/2018.
 */
public final class Magnitude implements Serializable{

    private static final long serialVersionUID = -571080056907945480L;

    private final String unit;

    private final double value;

    public Magnitude(String unit, double value) {
        this.unit = unit;
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Magnitude magnitude = (Magnitude) o;

        if (Double.compare(magnitude.value, value) != 0) return false;
        return !(unit != null ? !unit.equals(magnitude.unit) : magnitude.unit != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = unit != null ? unit.hashCode() : 0;
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Magnitude{");
        sb.append("unit='").append(unit).append('\'');
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
