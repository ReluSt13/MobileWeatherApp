package com.relustatescu.weatherapp.domain.weather

import java.time.LocalDateTime

data class WeatherData(
    val time: LocalDateTime,
    val temperatureC: Double,
    val apparentTemperatureC: Double,
    val weatherType: WeatherType,
    val pressure: Double,
    val windSpeed: Double,
    val humidity: Double,
    val precipitationProbability: Double
)
