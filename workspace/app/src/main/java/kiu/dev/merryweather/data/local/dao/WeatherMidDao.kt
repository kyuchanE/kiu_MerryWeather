package kiu.dev.merryweather.data.local.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import kiu.dev.merryweather.data.local.weather.mid.WeatherMid
import kiu.dev.merryweather.data.local.weather.now.WeatherNow

@Dao
interface WeatherMidDao {

    @Query("SELECT * FROM weather_mid order by date asc")
    fun getWeatherData(): Flowable<List<WeatherMid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeatherData(vararg weather: WeatherMid): Completable

    @Update
    fun updateWeatherData(weather: WeatherMid): Completable

    @Delete
    fun deleteWeatherData(weather: WeatherMid): Completable

    @Delete
    fun deleteWeatherData(vararg weather: WeatherMid): Completable

    @Query("DELETE FROM weather_mid")
    fun deleteAll(): Completable
}