package com.relustatescu.weatherapp.domain.location

data class PlaceAutocompleteInfo (
    val places: List<Place>
)

data class Place(
    val description: String,
    val place_id: String
)