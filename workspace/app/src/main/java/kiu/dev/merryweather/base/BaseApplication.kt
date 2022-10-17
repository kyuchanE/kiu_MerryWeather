package kiu.dev.merryweather.base

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication: Application() {

    companion object {
        const val BASE_URL = "http://api.github.com/"
    }

    override fun onCreate() {
        super.onCreate()

    }
}