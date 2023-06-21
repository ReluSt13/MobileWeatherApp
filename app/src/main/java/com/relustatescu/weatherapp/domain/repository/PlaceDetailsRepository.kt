package com.relustatescu.weatherapp.domain.repository

import com.relustatescu.weatherapp.data.remote.Location
import com.relustatescu.weatherapp.domain.util.Resource

interface PlaceDetailsRepository {
    suspend fun getDetails(place_id: String): Resource<Location>
}