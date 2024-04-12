package dev.kyu.domain.usecase

import dev.kyu.domain.model.VilageFcstData
import dev.kyu.domain.model.WeatherData
import dev.kyu.domain.repository.DatabaseRepository
import dev.kyu.domain.repository.WeatherRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVilageWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val databaseRepository: DatabaseRepository,
) {

    /**
     * 초단기 예보 조회
     */
    fun getUltraStrFcstData(
        numOfRows: Int,
        pageNo: Int,
        nx: Int,
        ny: Int,
        baseDate: String,
        baseTime: String,
    ): Flow<VilageFcstData?> = weatherRepository.getUltraStrFcst(
        numOfRows,
        pageNo,
        nx,
        ny,
        baseDate,
        baseTime
    )

    /**
     *  단기 예보
     */
    fun getVilageFcstData(
        numOfRows: Int,
        pageNo: Int,
        nx: Int,
        ny: Int,
        baseDate: String,
        baseTime: String,
    ): Flow<VilageFcstData?> = weatherRepository.getVilageFcst(
        numOfRows,
        pageNo,
        nx,
        ny,
        baseDate,
        baseTime
    )

    fun saveWeatherData(
        weatherData: WeatherData
    ) {
        databaseRepository.saveWeatherData(weatherData)
    }

    fun getAllWeatherData(): List<WeatherData> = databaseRepository.getAllWeatherData()

    fun getRealm(): Realm = databaseRepository.getRealm()
}