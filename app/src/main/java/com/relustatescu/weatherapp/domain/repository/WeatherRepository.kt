package com.relustatescu.weatherapp.domain.repository

import com.relustatescu.weatherapp.domain.util.Resource
import com.relustatescu.weatherapp.domain.weather.WeatherInfo

interface WeatherRepository {
    suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo>
}