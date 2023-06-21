package com.relustatescu.weatherapp.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.relustatescu.weatherapp.domain.location.LocationTracker
import com.relustatescu.weatherapp.domain.location.Place
import com.relustatescu.weatherapp.domain.location.PlaceAutocompleteInfo
import com.relustatescu.weatherapp.domain.repository.GeocodingRepository
import com.relustatescu.weatherapp.domain.repository.PlaceAutocompleteRepository
import com.relustatescu.weatherapp.domain.repository.PlaceDetailsRepository
import com.relustatescu.weatherapp.domain.repository.WeatherRepository
import com.relustatescu.weatherapp.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker,
    private val geocodingRepository: GeocodingRepository,
    private val placeAutocompleteRepository: PlaceAutocompleteRepository,
    private val placeDetailsRepository: PlaceDetailsRepository
): ViewModel() {
    var state by mutableStateOf(WeatherState())
        private set

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private var _cities = MutableStateFlow(emptyList<Place>())
    val cities = searchText
        .debounce(500L)
        .onEach { _isSearching.update { true } }
        .combine(_cities) { text, cities ->
            when(val result = placeAutocompleteRepository.getPlaces(text)){
                is Resource.Success -> {
                    Log.d("TAG", result.data?.places.toString())
                    result.data?.places
                }
                is Resource.Error -> {
                    null
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _cities.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        Log.d("ONSEARCHTEXTCHANGE", cities.value.toString())
    }

    fun loadWeatherInfo() {
        viewModelScope.launch {
            coroutineScope {
                launch {
                    state = state.copy(
                        isLoading = true,
                        error = null
                    )
                    locationTracker.getCurrentLocation()?.let { location ->
                        when (val result = repository.getWeatherData(location.latitude, location.longitude)) {
                            is Resource.Success -> {
                                state = state.copy(
                                    weatherInfo = result.data,
                                    coords = LatLng(location.latitude, location.longitude),
                                    isLoading = false,
                                    error = null
                                )
                            }

                            is Resource.Error -> {
                                state = state.copy(
                                    weatherInfo = null,
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    } ?: kotlin.run {
                        state = state.copy(
                            isLoading = false,
                            error = "Couldn't retrieve location..."
                        )
                    }
                }
                launch {
                    state = state.copy(
                        isLoading = true,
                        error = null
                    )
                    locationTracker.getCurrentLocation()?.let { location ->
                        when(val result = geocodingRepository.getReverseGeocoding(location.latitude, location.longitude)) {
                            is Resource.Success -> {
                                state = state.copy(
                                    locationInfo = result.data,
                                    isLoading = false,
                                    error = null
                                )
                            }
                            is Resource.Error -> {
                                state = state.copy(
                                    locationInfo = null,
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun loadWeatherOfSelected(placeId: String) {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )
            when(val result = placeDetailsRepository.getDetails(placeId)) {
                is Resource.Success -> {
                    getWeatherFromMap(LatLng(result.data?.lat!!, result.data?.lng!!))
                }
                is Resource.Error -> {
                    state = state.copy(
                        weatherInfo = null,
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun getWeatherFromMap(latLng: LatLng) {
        viewModelScope.launch {
            coroutineScope {
                launch {
                    state = state.copy(
                        isLoading = true,
                        error = null
                    )
                    when(val result = repository.getWeatherData(latLng.latitude, latLng.longitude)) {
                        is Resource.Success -> {
                            state = state.copy(
                                weatherInfo = result.data,
                                coords = latLng,
                                isLoading = false,
                                error = null
                            )
                        }
                        is Resource.Error -> {
                            state = state.copy(
                                weatherInfo = null,
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
                launch {
                    state = state.copy(
                        isLoading = true,
                        error = null
                    )
                    when(val result = geocodingRepository.getReverseGeocoding(latLng.latitude, latLng.longitude)) {
                        is Resource.Success -> {
                            state = state.copy(
                                locationInfo = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                        is Resource.Error -> {
                            state = state.copy(
                                locationInfo = null,
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }
}