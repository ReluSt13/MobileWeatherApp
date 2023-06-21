package com.relustatescu.weatherapp.presentation

import com.google.android.gms.maps.model.LatLng
import com.relustatescu.weatherapp.domain.location.GeocodingInfo
import com.relustatescu.weatherapp.domain.weather.WeatherInfo

data class WeatherState(
    val weatherInfo: WeatherInfo? = null,
    val locationInfo: GeocodingInfo? = null,
    val coords: LatLng? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
