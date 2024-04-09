package dev.kyu.data.repository

import dev.kyu.domain.model.WeatherData
import dev.kyu.domain.repository.DatabaseRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val realm: Realm,
): DatabaseRepository {

    override fun saveWeatherData(weatherData: WeatherData) {
        realm.writeBlocking {
            copyToRealm(weatherData)
        }
    }

    override fun getAllWeatherData(): List<WeatherData> = realm.query<WeatherData>().find()

}