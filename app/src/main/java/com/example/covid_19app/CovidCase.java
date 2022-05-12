package com.example.covid_19app;

public class CovidCase {
    private String timestamp, humidity, ambient_temperature;
    private double latitude, longitude;

    public CovidCase(){}

    public CovidCase(String timestamp, String humidity, String ambient_temperature, double latitude, double longitude) {
        this.timestamp = timestamp;
        this.humidity = humidity;
        this.ambient_temperature = ambient_temperature;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

//    public void setTimestamp(String timestamp) {
//        this.timestamp = timestamp;
//    }

    public String getHumidity() {
        return humidity;
    }

//    public void setHumidity(String humidity) {
//        this.humidity = humidity;
//    }

    public String getAmbient_temperature() {
        return ambient_temperature;
    }

//    public void setAmbient_temperature(String ambient_temperature) {
//        this.ambient_temperature = ambient_temperature;
//    }

    public double getLatitude() {
        return latitude;
    }

//    public void setLatitude(double latitude) {
//        this.latitude = latitude;
//    }

    public double getLongitude() {
        return longitude;
    }

//    public void setLongitude(double longitude) {
//        this.longitude = longitude;
//    }
}
