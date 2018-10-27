package com.finleap.restapp.forecastweather.service;

import com.finleap.restapp.forecastweather.model.City;
import com.finleap.restapp.forecastweather.model.ForecastWeatherMetrics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

/**
 * Test case for validating the proper functionality of ForecastWeatherService
 * Created by Liodegar on 10/14/2018.
 */
@RunWith(SpringRunner.class)
public class ForecastWeatherServiceImplTest {
    @Mock
    private SourceProviderFactory sourceProviderFactory;

    @Spy
    @InjectMocks
    private ForecastWeatherServiceImpl systemUnderTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testGetForecastWeatherMetrics() throws Exception {
        //given conditions
        City city = new City(3647637L);
        city.setSourceProviderKey("openweathermap.sourceProviderKey");

        SourceProvider sourceProvider = new AbstractSourceProvider(){

            @Override
            public ForecastWeatherMetrics getForecastWeatherMetrics(City city) {
                return new ForecastWeatherMetrics(city);
            }
        };

        //when
        doReturn(sourceProvider).when(sourceProviderFactory).getSourceProvider(anyString());

        //validate
        ForecastWeatherMetrics forecastWeatherMetrics = systemUnderTest.getForecastWeatherMetrics(city);
        assertNotNull(forecastWeatherMetrics);

    }
}