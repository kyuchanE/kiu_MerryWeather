package kiu.dev.merryweather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import kiu.dev.merryweather.data.local.dao.WeatherDao

@Database(entities = [Weather::class], version = 1, exportSchema = false)
abstract class WeatherDataBase: RoomDatabase() {
    abstract fun weatherDAO(): WeatherDao
}