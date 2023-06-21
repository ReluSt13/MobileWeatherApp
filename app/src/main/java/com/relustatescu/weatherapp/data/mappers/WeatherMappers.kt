package com.relustatescu.weatherapp.data.mappers

import com.relustatescu.weatherapp.data.remote.WeatherDTO
import com.relustatescu.weatherapp.data.remote.WeatherDataDTO
import com.relustatescu.weatherapp.domain.weather.WeatherData
import com.relustatescu.weatherapp.domain.weather.WeatherInfo
import com.relustatescu.weatherapp.domain.weather.WeatherType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class IndexedWeatherData(
    val index: Int,
    val data: WeatherData
)

fun WeatherDataDTO.toWeatherDataMap(): Map<Int, List<WeatherData>> {
    return time.mapIndexed { index, time ->
        val temperature = temperatures[index]
        val apparentTemperature = apparentTemperatures[index]
        val weatherCode = weatherCodes[index]
        val pressure = pressures[index]
        val windSpeed = windSpeeds[index]
        val humidity = humidities[index]
        val precipitationProbability = precipitationProbabilities[index]
        IndexedWeatherData(
            index = index,
            data = WeatherData(
                time = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME),
                temperatureC = temperature,
                apparentTemperatureC = apparentTemperature,
                weatherType = WeatherType.fromWMO(weatherCode),
                pressure = pressure,
                windSpeed = windSpeed,
                humidity = humidity,
                precipitationProbability = precipitationProbability
            )
        )
    }.groupBy {
        it.index / 24
    }.mapValues {
        it.value.map { it.data }
    }
}

fun WeatherDTO.toWeatherInfo(): WeatherInfo {
    val weatherDataMap = weatherData.toWeatherDataMap()
    val now = LocalDateTime.now()
    val currentWeatherData = weatherDataMap[0]?.find { it.time.hour == now.hour }
    return WeatherInfo(
        weatherDataPerDay = weatherDataMap,
        currentWeatherData = currentWeatherData
    )
}