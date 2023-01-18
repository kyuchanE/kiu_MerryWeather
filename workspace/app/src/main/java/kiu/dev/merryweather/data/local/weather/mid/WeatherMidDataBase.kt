package kiu.dev.merryweather.data.local.weather.mid

import androidx.room.Database
import androidx.room.RoomDatabase
import kiu.dev.merryweather.data.local.dao.WeatherMidDao

@Database(entities = [WeatherMid::class], version = 1, exportSchema = false)
abstract class WeatherMidDataBase: RoomDatabase() {
    abstract fun weatherMidDAO(): WeatherMidDao
}