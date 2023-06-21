package com.relustatescu.weatherapp.data.remote

data class PlaceAutocompleteDTO(
    val predictions: List<Prediction>
)

data class Prediction(
    val description: String,
    val matched_substrings: List<Substrings>,
    val place_id: String,
    val reference: String,
    val structured_formatting: StructuredFormatting,
    val terms: List<Term>,
    val types: List<String>
)

data class Substrings(
    val length: Int,
    val offset: Int
)

data class StructuredFormatting(
    val main_text: String,
    val main_text_matched_substring: List<Substrings>,
    val secondary_text: String
)

data class Term(
    val offset: Int,
    val value: String
)
