package kiu.dev.merryweather.base

import android.app.Application
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp
import kiu.dev.merryweather.di.NetworkModule
import kiu.dev.merryweather.di.activityModule
import kiu.dev.merryweather.di.apiModule
import kiu.dev.merryweather.di.weatherRepositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
        NetworkModule.init(this)

        // Koin
        startKoin {
            androidContext(this@BaseApplication)
            modules(
                activityModule,
                apiModule,
                weatherRepositoryModule
            )
        }

    }
}