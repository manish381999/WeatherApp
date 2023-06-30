package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.databinding.ActivityMainBinding;
import com.example.weatherapp.databinding.WeatherItemLayoutBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;

private ArrayList<WeatherModel> list;
private WeatherAdapter weatherAdapter;

private LocationManager locationManager;
private int PERMISSION_CODE =1;

private String cityName;
    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(binding.getRoot());

        list=new ArrayList<>();

        weatherAdapter=new WeatherAdapter(this,list);
        binding.rvWeather.setAdapter(weatherAdapter);


        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);

        }
        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName=getCityName(location.getLongitude(),location.getLatitude());

        getWeatherInfo(cityName);

        binding.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city=binding.edtCity.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                }else {
                    binding.cityName.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission  granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Please provide the permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityName = "Not found";
        Geocoder gcd =new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses=gcd.getFromLocation(latitude,longitude,10);

            for (Address adr : addresses){
                if (adr!=null){
                    String city =adr.getLocality();
                    if (city!=null && !city.equals("")){
                        cityName=city;
                    }else {
                        Log.d("TAG", "City Not Found");
                        Toast.makeText(this, "User city not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName){
        String url ="http://api.weatherapi.com/v1/forecast.json?key=103487af734245e4afa72116233006&q=" + cityName + " &days=1&aqi=no&alerts=no\n";
        binding.cityName.setText(cityName);
        RequestQueue  requestQueue= Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(JSONObject response) {
                binding.pb.setVisibility(View.GONE);
                binding.home.setVisibility(View.VISIBLE);
                list.clear();

                try {
                    String temperature =response.getJSONObject("current").getString("temp_c");
                    binding.tvTemperature.setText(temperature+"°c");
                    int isDay= response.getJSONObject("current").getInt("is_day");
                    String condition=response.getJSONObject("current").getJSONObject("current").getString("text");
                    String conditionIcon=response.getJSONObject("current").getJSONObject("current").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(binding.ivicon);
                    binding.tvCondition.setText(condition);
                    if (isDay==1){
                        //morning
                        Picasso.get().load("https://images.unsplash.com/photo-1593978301851-40c1849d47d4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=327&q=80").into(binding.ivBack);
                    }else {
                        Picasso.get().load("https://images.unsplash.com/photo-1612928644160-fd93db72c626?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=870&q=80").into(binding.ivBack);
                    }


                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forcast0= forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray=forcast0.getJSONArray("hour");

                    for (int i=0; i<hourArray.length(); i++){
                        JSONObject hourObj=hourArray.getJSONObject(i);
                        String time =hourObj.getString("time");
                        String temper =hourObj.getString("temp_c");
                        String img =hourObj.getJSONObject("condition").getString("icon");
                        String wind =hourObj.getString("wind_kph");
                        list.add(new WeatherModel(time,temper,img,wind));
                    }
                       weatherAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });


        requestQueue.add(jsonObjectRequest);
    }
}