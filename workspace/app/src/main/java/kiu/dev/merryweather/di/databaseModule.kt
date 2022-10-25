package kiu.dev.merryweather.di

import androidx.room.Room
import kiu.dev.merryweather.data.local.WidgetIdDataBase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            WidgetIdDataBase::class.java,
            "widget_id"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        get<WidgetIdDataBase>().widgetIdDAO()
    }
}