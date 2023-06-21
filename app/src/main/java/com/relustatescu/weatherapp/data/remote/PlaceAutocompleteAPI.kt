package com.relustatescu.weatherapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceAutocompleteAPI {
    @GET("maps/api/place/autocomplete/json?types=(cities)")
    suspend fun autocomplete(
        @Query("input") input: String,
        @Query("key") apiKey: String
    ): PlaceAutocompleteDTO
}