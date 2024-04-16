package dev.kyu.domain.usecase

import com.google.gson.JsonObject
import dev.kyu.domain.model.MidLandFcstData
import dev.kyu.domain.model.MidTaData
import dev.kyu.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMidWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {

    /**
     * 중기 육상 예보
     */
    fun getMidLandFcsData(
        numOfRows: Int,
        pageNo: Int,
        regId: String,
        tmFc: String,
    ): Flow<MidLandFcstData?> = weatherRepository.getMidLandFcstData(
        numOfRows,
        pageNo,
        regId,
        tmFc
    )

    /**
     * 중기 기온
     */
    fun getMidTaData(
        numOfRows: Int,
        pageNo: Int,
        regId: String,
        tmFc: String,
    ): Flow<MidTaData?> = weatherRepository.getMidTaData(
        numOfRows,
        pageNo,
        regId,
        tmFc
    )


}