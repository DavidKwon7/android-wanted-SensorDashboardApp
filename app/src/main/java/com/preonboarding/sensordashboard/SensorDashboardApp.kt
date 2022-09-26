package com.preonboarding.sensordashboard

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class SensorDashboardApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant()
    }
}