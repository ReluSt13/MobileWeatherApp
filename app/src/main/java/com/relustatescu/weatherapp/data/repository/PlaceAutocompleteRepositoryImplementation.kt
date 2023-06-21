package com.relustatescu.weatherapp.data.repository

import com.relustatescu.weatherapp.data.remote.PlaceAutocompleteAPI
import com.relustatescu.weatherapp.domain.location.GeocodingInfo
import com.relustatescu.weatherapp.domain.location.Place
import com.relustatescu.weatherapp.domain.location.PlaceAutocompleteInfo
import com.relustatescu.weatherapp.domain.repository.PlaceAutocompleteRepository
import com.relustatescu.weatherapp.domain.util.Resource
import javax.inject.Inject

class PlaceAutocompleteRepositoryImplementation @Inject constructor(
    private val api: PlaceAutocompleteAPI
): PlaceAutocompleteRepository {
    override suspend fun getPlaces(input: String): Resource<PlaceAutocompleteInfo> {
        return try {
            val apiKey = "GOOGLE_API_KEY"
            val results = api.autocomplete(
                input = input,
                apiKey = apiKey
            ).predictions
            val places = mutableListOf<Place>()
            for (result in results) {
                places.add(Place(result.description, result.place_id))
            }
            Resource.Success(
                data = PlaceAutocompleteInfo(places)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occured.")
        }
    }
}