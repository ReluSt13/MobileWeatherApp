package com.relustatescu.weatherapp.di

import com.relustatescu.weatherapp.data.DefaultLocationTracker
import com.relustatescu.weatherapp.data.repository.WeatherRepositoryImplementation
import com.relustatescu.weatherapp.domain.location.LocationTracker
import com.relustatescu.weatherapp.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImplementation: WeatherRepositoryImplementation
    ): WeatherRepository
}