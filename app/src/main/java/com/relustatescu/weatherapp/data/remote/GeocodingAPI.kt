package com.relustatescu.weatherapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingAPI {
    @GET("maps/api/geocode/json")
    suspend fun reverseGeocode(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String
    ): GeocodingDTO
}