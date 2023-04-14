package kiu.dev.merryweather.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kiu.dev.merryweather.data.BasicApi
import kiu.dev.merryweather.data.local.dao.WeatherMidDao
import kiu.dev.merryweather.data.local.dao.WeatherNowDao
import kiu.dev.merryweather.data.repository.WeatherRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
object WeatherRepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideWeatherRepository(
        basicApi: BasicApi,
        weatherNowDao: WeatherNowDao,
        weatherMidDao: WeatherMidDao
    ) = WeatherRepository(basicApi, weatherNowDao, weatherMidDao)
}