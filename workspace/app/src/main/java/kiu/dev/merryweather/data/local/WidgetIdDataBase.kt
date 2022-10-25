package kiu.dev.merryweather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import kiu.dev.merryweather.data.local.dao.WidgetIdDao

@Database(entities = [WidgetId::class], version = 1, exportSchema = false)
abstract class WidgetIdDataBase: RoomDatabase() {
    abstract fun widgetIdDAO():WidgetIdDao
}