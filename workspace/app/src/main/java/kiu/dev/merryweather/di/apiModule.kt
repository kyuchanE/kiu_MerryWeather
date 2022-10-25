package kiu.dev.merryweather.di

import kiu.dev.merryweather.data.BasicApi
import org.koin.dsl.module

val apiModule = module {
    single {
        NetworkModule.build().create(BasicApi::class.java)
    }
}