package com.relustatescu.weatherapp.data.remote

import com.squareup.moshi.Json

data class GeocodingDTO(
    val results: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    @field:Json(name = "address_components")
    val addressComponents: List<AddressComponent>,
    @field:Json(name = "formatted_address")
    val formattedAddress: String,
    val geometry: Geometry,
    @field:Json(name = "place_id")
    val placeId: String,
    @field:Json(name = "plus_code")
    val plusCode: PlusCode,
    val types: List<String>
)

data class AddressComponent(
    @field:Json(name = "long_name")
    val longName: String,
    @field:Json(name = "short_name")
    val shortName: String,
    val types: List<String>
)

data class Geometry(
    val bounds: Bounds?,
    val location: Location,
    @field:Json(name = "location_type")
    val locationType: String,
    val viewport: Viewport?
)

data class PlusCode(
    @field:Json(name = "compound_code")
    val compoundCode: String,
    @field:Json(name = "global_code")
    val globalCode: String
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Bounds(
    val northeast: Location,
    val southwest: Location
)

data class Viewport(
    val northeast: Location,
    val southwest: Location
)