package kiu.dev.merryweather.data.local.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import kiu.dev.merryweather.data.local.weather.now.WeatherNow

@Dao
interface WeatherNowDao {

    @Query("SELECT * FROM weather_now order by time asc")
    fun getWeatherData(): Flowable<List<WeatherNow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeatherData(vararg weather: WeatherNow): Completable

    @Update
    fun updateWeatherData(weather: WeatherNow): Completable

    @Delete
    fun deleteWeatherData(weather: WeatherNow): Completable

    @Delete
    fun deleteWeatherData(vararg weather: WeatherNow): Completable

    @Query("DELETE FROM weather_now")
    fun deleteAll(): Completable
}