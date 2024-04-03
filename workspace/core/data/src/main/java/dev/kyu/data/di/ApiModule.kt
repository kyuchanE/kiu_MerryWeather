package dev.kyu.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.kyu.data.api.WeatherApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideWeatherApiService(provideWeatherRetrofit: Retrofit): WeatherApi =
        provideWeatherRetrofit.create(WeatherApi::class.java)

}