package com.soumen.weatherapp;

public class WeatherResponse {
    public Main main;
    public Wind wind;
    public Rain bristy;

    public class Rain {
        public float rain;
    }

    public class Wind {
        public float deg;
        public float speed;
    }

    public class Main {
        public float temp;
        public float pressure;
        public float humidity;
        public float clouds;
    }
}
