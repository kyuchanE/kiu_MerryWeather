package kiu.dev.merryweather.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kiu.dev.merryweather.model.BasicApi
import org.koin.dsl.module
import retrofit2.Retrofit
import javax.inject.Singleton

val apiModule = module {
    single {
        NetworkModule.build().create(BasicApi::class.java)
    }
}