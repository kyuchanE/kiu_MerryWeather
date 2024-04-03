package dev.kyu.domain.repository

import dev.kyu.domain.model.MidLandFcstData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun getMidLandFcstData(
        numOfRows: Int,
        pageNo: Int,
        regId: String,
        tmFc: String,
    ): Flow<MidLandFcstData?>
}