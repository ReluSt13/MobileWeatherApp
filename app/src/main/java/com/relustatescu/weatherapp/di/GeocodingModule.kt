package com.relustatescu.weatherapp.di

import com.relustatescu.weatherapp.data.repository.GeocodingRepositoryImplementation
import com.relustatescu.weatherapp.domain.repository.GeocodingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GeocodingModule {
    @Binds
    @Singleton
    abstract fun bindGeocodingRepository(
        geocodingRepositoryImplementation: GeocodingRepositoryImplementation
    ): GeocodingRepository
}