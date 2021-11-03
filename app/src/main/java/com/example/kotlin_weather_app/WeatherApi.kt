package com.example.kotlin_weather_app

import com.example.kotlin_weather_app.Response.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface WeatherApi {

    @Headers(
        "x-rapidapi-host: community-open-weather-map.p.rapidapi.com",
        "x-rapidapi-key: 13007ca11amshb22e5b57dccaa9fp162835jsn6aa30b736b15"
    )
    @GET("weather")
    fun getWeather(
        @Query("lat") lat:Double,
        @Query("lon") lon:Double,
    ):Call<WeatherResponse>
}