package com.relustatescu.weatherapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceDetailsAPI {
    @GET("maps/api/place/details/json")
    suspend fun getDetails(
        @Query("placeid") place_id: String,
        @Query("key") apiKey: String
    ): PlaceDetailsDTO
}