package dev.kyu.domain.repository

import dev.kyu.domain.model.WeatherData
import io.realm.kotlin.Realm
import io.realm.kotlin.query.RealmResults

interface DatabaseRepository {

    fun saveWeatherData(weatherData: WeatherData,)

    fun getAllWeatherData(): List<WeatherData>

    fun getRealm(): Realm
}