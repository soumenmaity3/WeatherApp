package com.soumen.weatherapp;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherService{
    private static final String API_KEY="eb32c80722022ddb4d78bf036d504117";
    private static final String BASE_URL="https://api.openweathermap.org/data/2.5/weather";
    public WeatherResponse getWeather(String city) throws Exception{
        OkHttpClient client=new OkHttpClient();
        String url = BASE_URL + "?q=" + city + "&appid=" + API_KEY + "&units=metric";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            Gson gson = new Gson();
            return gson.fromJson(response.body().string(), WeatherResponse.class);
        }
    }
}
