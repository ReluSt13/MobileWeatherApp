package com.relustatescu.weatherapp.data.repository

import com.relustatescu.weatherapp.data.mappers.toWeatherInfo
import com.relustatescu.weatherapp.data.remote.GeocodingAPI
import com.relustatescu.weatherapp.domain.location.GeocodingInfo
import com.relustatescu.weatherapp.domain.repository.GeocodingRepository
import com.relustatescu.weatherapp.domain.util.Resource
import javax.inject.Inject

class GeocodingRepositoryImplementation @Inject constructor(
    private val api: GeocodingAPI
): GeocodingRepository {
    override suspend fun getReverseGeocoding(lat: Double, long: Double): Resource<GeocodingInfo> {
        return try {
            val latlng = "$lat,$long"
            val apiKey = "GOOGLE_API_KEY"
            val results = api.reverseGeocode(
                latlng = latlng,
                apiKey = apiKey
            ).results
            var city: String? = null
            var country: String? = null
            for (result in results) {
                for (component in result.addressComponents) {
                    if (component.types.contains("locality") || component.types.contains("administrative_area_level_1")) {
                        city = component.longName
                    } else if (component.types.contains("country")) {
                        country = component.longName
                    }
                }
                if (city != null && country != null) {
                    break
                }
            }

            val cityName = city ?: "Unknown City"
            val countryName = country ?: "Unknown Country"
            Resource.Success(
                data = GeocodingInfo("$cityName, $countryName")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occured.")
        }

    }
}