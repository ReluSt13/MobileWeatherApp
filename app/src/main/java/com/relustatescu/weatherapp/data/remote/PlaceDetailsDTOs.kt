package com.relustatescu.weatherapp.data.remote

data class PlaceDetailsDTO(
    val html_attributions: List<String>,
    val result: Result,
    val status: String

)

data class Result(
    val address_components: List<AddressComponent>,
    val adr_address: String,
    val formatted_address: String,
    val geometry: Geometry,
    val icon: String,
    val icon_background_color: String,
    val icon_mask_base_uri: String,
    val name: String,
    val photos: List<Photo>,
    val place_id: String,
    val reference: String,
    val types: List<String>,
    val url: String,
    val utc_offset: Int,
    val vicinity: String,
    val website: String
)

data class Photo(
    val height: Int,
    val html_attributions: List<String>,
    val photo_reference: String,
    val width: Int
)
