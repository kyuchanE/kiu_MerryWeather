package kiu.dev.merryweather.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kiu.dev.merryweather.data.AirApi
import kiu.dev.merryweather.data.BasicApi
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun baseApi(@NetworkModule.WeatherRetrofit retrofit: Retrofit): BasicApi = retrofit.create(BasicApi::class.java)

    @Provides
    @Singleton
    fun baseAirApi(@NetworkModule.AirRetrofit retrofit: Retrofit): AirApi = retrofit.create(AirApi::class.java)
}