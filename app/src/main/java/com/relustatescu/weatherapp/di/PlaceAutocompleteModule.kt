package com.relustatescu.weatherapp.di

import com.relustatescu.weatherapp.data.repository.PlaceAutocompleteRepositoryImplementation
import com.relustatescu.weatherapp.domain.repository.PlaceAutocompleteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlaceAutocompleteModule {
    @Binds
    @Singleton
    abstract fun bindPlaceAutocompleteRepository(
        placeAutocompleteRepositoryImplementation: PlaceAutocompleteRepositoryImplementation
    ): PlaceAutocompleteRepository
}