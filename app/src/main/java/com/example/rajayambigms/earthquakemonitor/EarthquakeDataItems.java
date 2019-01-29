package com.example.rajayambigms.earthquakemonitor;

public class EarthquakeDataItems {

    int hourlyWeatherImage;
    String hourlWeatherData;


    public EarthquakeDataItems(int img, String hrdata) {
        this.hourlWeatherData = hrdata;
        this.hourlyWeatherImage = img;
    }
}
