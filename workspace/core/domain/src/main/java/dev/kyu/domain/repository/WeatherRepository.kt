package dev.kyu.domain.repository

import com.google.gson.JsonObject
import dev.kyu.domain.model.MidLandFcstData
import dev.kyu.domain.model.MidTaData
import dev.kyu.domain.model.VilageFcstData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun getMidLandFcstData(
        numOfRows: Int,
        pageNo: Int,
        regId: String,
        tmFc: String,
    ): Flow<MidLandFcstData?>

    fun getMidTaData(
        numOfRows: Int,
        pageNo: Int,
        regId: String,
        tmFc: String,
    ): Flow<MidTaData?>

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