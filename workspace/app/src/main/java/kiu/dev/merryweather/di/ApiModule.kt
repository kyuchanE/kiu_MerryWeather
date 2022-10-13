package kiu.dev.merryweather.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kiu.dev.merryweather.model.BasicApi
import retrofit2.Retrofit
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object ApiModule {
//
//    @Provides
//    @Singleton
//    fun baseApi(retrofit: Retrofit): BasicApi = retrofit.create(BasicApi::class.java)
//}