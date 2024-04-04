package dev.kyu.domain.repository

import dev.kyu.domain.model.MidLandFcstData
import dev.kyu.domain.model.VilageFcstData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun getMidLandFcstData(
        numOfRows: Int,
        pageNo: Int,
        regId: String,
        tmFc: String,
    ): Flow<MidLandFcstData?>

    fun getVilageFcst(
        numOfRows: Int,
        pageNo: Int,
        nx: Int,
        ny: Int,
        baseDate: String,
        baseTime: String,
    ): Flow<VilageFcstData?>

    fun getUltraStrFcst(
        numOfRows: Int,
        pageNo: Int,
        nx: Int,
        ny: Int,
        baseDate: String,
        baseTime: String,
    ): Flow<VilageFcstData?>
}