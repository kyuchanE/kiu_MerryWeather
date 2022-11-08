package kiu.dev.merryweather.data.local.weather.now

import androidx.room.Database
import androidx.room.RoomDatabase
import kiu.dev.merryweather.data.local.dao.WeatherNowDao

@Database(entities = [WeatherNow::class], version = 1, exportSchema = false)
abstract class WeatherDataBase: RoomDatabase() {
    abstract fun weatherDAO(): WeatherNowDao
}