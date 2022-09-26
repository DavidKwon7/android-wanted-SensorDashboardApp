package com.preonboarding.sensordashboard.di

import com.preonboarding.sensordashboard.data.repository.MeasurementRepositoryImpl
import com.preonboarding.sensordashboard.domain.repository.MeasurementRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideRepository(measurementRepositoryImpl: MeasurementRepositoryImpl): MeasurementRepository
}