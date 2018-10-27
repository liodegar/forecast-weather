package com.finleap.restapp.forecastweather.service.dto;

/**
 * Created by Liodegar on 10/26/2018.
 */
public class MockyCity {
    private int id;
    private String name;
    private Coordinate coord;
    private String country;
    private int population;

    public int getId() { return this.id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    public Coordinate getCoordinate() { return this.coord; }

    public void setCoordinate(Coordinate coord) { this.coord = coord; }

    public String getCountry() { return this.country; }

    public void setCountry(String country) { this.country = country; }

    public int getPopulation() { return this.population; }

    public void setPopulation(int population) { this.population = population; }
}
