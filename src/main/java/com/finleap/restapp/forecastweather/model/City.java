package com.finleap.restapp.forecastweather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created by Liodegar on 10/13/2018.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class City implements Serializable {

    private static final long serialVersionUID = 8483342465956521524L;

    private Long id;

    private String name;

    private String isoCountryCode;

    private String sourceProviderKey;

    public City() {
    }

    public City(Long id) {
        this.id = id;
    }

    public City(String name, String isoCountryCode) {
        this.name = name;
        this.isoCountryCode = isoCountryCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getIsoCountryCode() {
        return isoCountryCode;
    }

    public void setIsoCountryCode(String isoCountryCode) {
        this.isoCountryCode = isoCountryCode;
    }

    public String getSourceProviderKey() {
        return sourceProviderKey;
    }

    public void setSourceProviderKey(String sourceProviderKey) {
        this.sourceProviderKey = sourceProviderKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        if (id != null ? !id.equals(city.id) : city.id != null) return false;
        if (name != null ? !name.equals(city.name) : city.name != null) return false;
        if (isoCountryCode != null ? !isoCountryCode.equals(city.isoCountryCode) : city.isoCountryCode != null)
            return false;
        return !(sourceProviderKey != null ? !sourceProviderKey.equals(city.sourceProviderKey) : city.sourceProviderKey != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (isoCountryCode != null ? isoCountryCode.hashCode() : 0);
        result = 31 * result + (sourceProviderKey != null ? sourceProviderKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("City{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", isoCountryCode='").append(isoCountryCode).append('\'');
        sb.append(", sourceProviderKey='").append(sourceProviderKey).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
