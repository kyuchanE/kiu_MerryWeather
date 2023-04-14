package kiu.dev.merryweather.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.local.weather.mid.WeatherMidDataBase
import kiu.dev.merryweather.data.local.weather.now.WeatherDataBase
import kiu.dev.merryweather.data.local.widget.WidgetIdDataBase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWeatherNowDatabase(
        @ApplicationContext context: Context
    ) =
        Room.databaseBuilder(
            context,
            WeatherDataBase::class.java,
            C.RoomTableName.WEATHER_NOW
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideWeatherNowDAO(dataBase: WeatherDataBase) = dataBase.weatherDAO()


    @Provides
    @Singleton
    fun provideWeatherMidDatabase(
        @ApplicationContext context: Context
    ) =
        Room.databaseBuilder(
            context,
            WeatherMidDataBase::class.java,
            C.RoomTableName.WEATHER_MID
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideWeatherMidDAO(dataBase: WeatherMidDataBase) = dataBase.weatherMidDAO()


    @Provides
    @Singleton
    fun provideWidgetIdDatabase(
        @ApplicationContext context: Context
    ) =
        Room.databaseBuilder(
            context,
            WidgetIdDataBase::class.java,
            C.RoomTableName.WIDGET_ID
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideWidgetDAO(dataBase: WidgetIdDataBase) = dataBase.widgetIdDAO()



}