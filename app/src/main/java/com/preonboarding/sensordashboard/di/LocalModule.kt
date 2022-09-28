package com.preonboarding.sensordashboard.di

import android.content.Context
import androidx.room.Room
import com.preonboarding.sensordashboard.data.converter.SensorListTypeConverter
import com.preonboarding.sensordashboard.data.database.AppDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideConverterMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideSensorListTypeConverter(moshi: Moshi): SensorListTypeConverter =
        SensorListTypeConverter(moshi)

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        moshi: Moshi,
        sensorListTypeConverter: SensorListTypeConverter,
        ): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "database")
            .addTypeConverter(sensorListTypeConverter)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFavoriteDAO(appDatabase: AppDatabase) = appDatabase.measurementDao()
}