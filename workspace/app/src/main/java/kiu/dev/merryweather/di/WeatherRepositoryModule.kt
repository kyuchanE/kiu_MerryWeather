package kiu.dev.merryweather.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kiu.dev.merryweather.model.BasicApi
import kiu.dev.merryweather.repository.WeatherRepository

//@Module
//@InstallIn(ActivityRetainedComponent::class)
//object WeatherRepositoryModule {
//
//    @Provides
//    @ActivityRetainedScoped
//    fun provideWeatherRepository(basicApi: BasicApi) = WeatherRepository(basicApi)
//}