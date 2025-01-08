package com.soumen.weatherapp;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import android.location.Location;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    TextInputEditText edtText;
    Button btnSearch, btnSearchLocation;
    TextView txtWeather;
    WeatherService weatherService;
    FusedLocationProviderClient fusedLocationClient;

    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtText = findViewById(R.id.idEditData);
        btnSearch = findViewById(R.id.buttonGetWeather);
        btnSearchLocation = findViewById(R.id.buttonGetLocation);
        txtWeather = findViewById(R.id.textViewWeather);

        weatherService = new WeatherService();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(this, "Internet is available", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtText.getText().toString().trim();
                getWeather(city);
                edtText.setText("");
                edtText.clearFocus();
            }
        });

        btnSearchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestLocationPermission();
            }
        });
    }

    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            getLocation();
        }
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            String cityName = getCityName(latitude, longitude);

                            getWeather(cityName);
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String getCityName(double latitude, double longitude) {
        String cityName = "Unknown City";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                cityName = addresses.get(0).getLocality(); // Get the city name
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Geocoder failed to fetch city name", Toast.LENGTH_SHORT).show();
        }
        return cityName;
    }

    private void getWeather(String city) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    WeatherResponse weather = weatherService.getWeather(city);

                    if (weather != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtWeather.setText("City name:"+city+"\nTemperature: " + weather.main.temp + " °C\n" +
                                        "Pressure: " + weather.main.pressure + " hPa\n" +
                                        "Humidity: " + weather.main.humidity + " %");
                            }
                        });
                    } else {
                        showError("City not found or invalid data");
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> showError("Failed to retrieve weather data"));
                }
            }
        });
    }

    private void showError(String errorMessage) {
        txtWeather.setText("I can't find.");
        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
