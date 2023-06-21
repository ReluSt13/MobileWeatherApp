package com.relustatescu.weatherapp.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.relustatescu.weatherapp.domain.location.LocationTracker
import com.relustatescu.weatherapp.domain.repository.WeatherRepository
import com.relustatescu.weatherapp.domain.util.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapViewModel: ViewModel() {

    var state by mutableStateOf(MapState())

    fun onEvent(event: MapEvent) {
        when(event) {
            is MapEvent.OnMapClick -> {
                viewModelScope.launch {

                }
            }
        }
    }
}