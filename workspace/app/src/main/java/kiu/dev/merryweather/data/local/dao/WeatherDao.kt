package kiu.dev.merryweather.data.local.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import kiu.dev.merryweather.data.local.Weather

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather_item order by time asc")
    fun getWeatherData(): Flowable<List<Weather>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeatherData(vararg weather: Weather): Completable

    @Update
    fun updateWeatherData(weather: Weather): Completable

    @Delete
    fun deleteWeatherData(weather: Weather): Completable

    @Delete
    fun deleteWeatherData(vararg weather: Weather): Completable

    @Query("DELETE FROM weather_item")
    fun deleteAll(): Completable
}