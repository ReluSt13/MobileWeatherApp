package com.relustatescu.weatherapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("v1/forecast?hourly=temperature_2m,relativehumidity_2m,apparent_temperature,precipitation_probability,weathercode,pressure_msl,windspeed_10m")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") long: Double
    ): WeatherDTO
}