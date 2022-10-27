package kiu.dev.merryweather.di

import kiu.dev.merryweather.data.repository.WeatherRepository
import kiu.dev.merryweather.data.repository.WidgetIdRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory {
        WeatherRepository(get(), get())
    }

    factory {
        WidgetIdRepository(get())
    }
}