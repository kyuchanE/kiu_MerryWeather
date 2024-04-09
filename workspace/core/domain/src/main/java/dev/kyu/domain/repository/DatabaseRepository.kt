package dev.kyu.domain.repository

import dev.kyu.domain.model.WeatherData

interface DatabaseRepository {

    fun saveWeatherData(weatherData: WeatherData,)

    fun getAllWeatherData(): List<WeatherData>
}