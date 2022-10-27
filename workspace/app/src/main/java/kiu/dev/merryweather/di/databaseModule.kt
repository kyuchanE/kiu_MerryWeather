package kiu.dev.merryweather.di

import androidx.room.Room
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.local.WeatherDataBase
import kiu.dev.merryweather.data.local.WidgetIdDataBase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            WidgetIdDataBase::class.java,
            C.RoomTableName.WIDGET_ID
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        get<WidgetIdDataBase>().widgetIdDAO()
    }

    single {
        Room.databaseBuilder(
            androidApplication(),
            WeatherDataBase::class.java,
            C.RoomTableName.WEATHER_ITEM
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        get<WeatherDataBase>().weatherDAO()
    }
}