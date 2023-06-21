package com.relustatescu.weatherapp.presentation

import com.google.android.gms.maps.model.LatLng

sealed class MapEvent {
    data class OnMapClick(val latLng: LatLng) : MapEvent()
}
