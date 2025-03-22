package com.soumen.weatherapp;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    public Main main;
    public Wind wind;
    @SerializedName("rain")
    public Rain bristy;
    public Clouds clouds;

    public class Rain {
        @SerializedName("1h")  // Correct mapping for rain in last 1 hour
        public float oneHourRain;
    }

    public class Wind {
        public float deg;
        public float speed;
    }

    public class Clouds {
        public int all;
    }

    public class Main {
        public float temp;
        public float pressure;
        public float humidity;
    }
}
