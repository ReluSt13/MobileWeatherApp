package com.relustatescu.weatherapp.data.repository

import com.relustatescu.weatherapp.data.remote.Location
import com.relustatescu.weatherapp.data.remote.PlaceDetailsAPI
import com.relustatescu.weatherapp.domain.repository.PlaceDetailsRepository
import com.relustatescu.weatherapp.domain.util.Resource
import javax.inject.Inject

class PlaceDetailsRepositoryImplementation @Inject constructor(
    private val api: PlaceDetailsAPI
): PlaceDetailsRepository {
    override suspend fun getDetails(place_id: String): Resource<Location> {
        return try {
            val apiKey = "GOOGLE_API_KEY"
            val location = api.getDetails(
                place_id = place_id,
                apiKey = apiKey
            ).result.geometry.location
            Resource.Success(
                data = location
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occured.")
        }
    }
}