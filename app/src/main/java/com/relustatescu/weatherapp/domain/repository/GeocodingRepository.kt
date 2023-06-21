package com.relustatescu.weatherapp.domain.repository

import com.relustatescu.weatherapp.domain.location.GeocodingInfo
import com.relustatescu.weatherapp.domain.util.Resource


interface GeocodingRepository {
    suspend fun getReverseGeocoding(lat: Double, long: Double): Resource<GeocodingInfo>
}