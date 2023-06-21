package com.relustatescu.weatherapp.data.repository

import com.relustatescu.weatherapp.data.mappers.toWeatherInfo
import com.relustatescu.weatherapp.data.remote.WeatherAPI
import com.relustatescu.weatherapp.domain.repository.WeatherRepository
import com.relustatescu.weatherapp.domain.util.Resource
import com.relustatescu.weatherapp.domain.weather.WeatherInfo
import javax.inject.Inject

class WeatherRepositoryImplementation @Inject constructor(
    private val api: WeatherAPI
): WeatherRepository {
    override suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo> {
        return try {
            Resource.Success(
                data = api.getWeather(
                    lat = lat,
                    long = long
                ).toWeatherInfo()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occured.")
        }
    }
}