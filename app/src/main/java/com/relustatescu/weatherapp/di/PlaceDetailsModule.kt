package com.relustatescu.weatherapp.di

import com.relustatescu.weatherapp.data.repository.PlaceDetailsRepositoryImplementation
import com.relustatescu.weatherapp.domain.repository.PlaceDetailsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlaceDetailsModule {
    @Binds
    @Singleton
    abstract fun bindPlaceDetailsRepository(
        placeDetailsRepositoryImplementation: PlaceDetailsRepositoryImplementation
    ): PlaceDetailsRepository
}