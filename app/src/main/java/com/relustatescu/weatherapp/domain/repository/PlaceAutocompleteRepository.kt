package com.relustatescu.weatherapp.domain.repository

import com.relustatescu.weatherapp.domain.location.PlaceAutocompleteInfo
import com.relustatescu.weatherapp.domain.util.Resource

interface PlaceAutocompleteRepository {
    suspend fun getPlaces(input: String): Resource<PlaceAutocompleteInfo>
}