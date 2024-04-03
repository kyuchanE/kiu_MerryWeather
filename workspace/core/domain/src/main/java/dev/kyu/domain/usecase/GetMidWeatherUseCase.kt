package dev.kyu.domain.usecase

import dev.kyu.domain.model.MidLandFcstData
import dev.kyu.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMidWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {

    operator fun invoke(
        numOfRows: Int,
        pageNo: Int,
        regId: String,
        tmFc: String
    ): Flow<MidLandFcstData?> = weatherRepository.getMidLandFcstData(
        numOfRows,
        pageNo,
        regId,
        tmFc
    )

}