package dev.kyu.data.repository

import android.util.Log
import com.google.gson.JsonObject
import dev.kyu.data.BuildConfig
import dev.kyu.data.api.WeatherApi
import dev.kyu.domain.model.MidLandFcstData
import dev.kyu.domain.model.MidTaData
import dev.kyu.domain.model.VilageFcstData
import dev.kyu.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
): WeatherRepository {

    // TODO chan 공통 에러 처리 필요
    override fun getMidLandFcstData(
        numOfRows: Int,
        pageNo: Int,
        regId: String,
        tmFc: String
    ): Flow<MidLandFcstData?> = flow {

        val midLandFcstResponse = weatherApi.reqMidLandFcst(
            serviceKey = BuildConfig.WEATHER_API_KEY,
            numOfRows = numOfRows,
            pageNo = pageNo,
            regId = regId,
            tmFc = tmFc,
            dataType = "JSON"
        )

        when(midLandFcstResponse.code()) {
            in 200 .. 299 -> emit(midLandFcstResponse.body()?.toDomain())
            else -> {
                Log.d("@@@@@@", "error ${midLandFcstResponse.errorBody().toString()}")
            }
        }
    }

    override fun getMidTaData(
        numOfRows: Int,
        pageNo: Int,
        regId: String,
        tmFc: String
    ): Flow<MidTaData?> = flow {

        val midTaResponse = weatherApi.reqMidTa(
            serviceKey = BuildConfig.WEATHER_API_KEY,
            numOfRows = numOfRows,
            pageNo = pageNo,
            regId = regId,
            tmFc = tmFc,
            dataType = "JSON"
        )

        when(midTaResponse.code()) {
            in 200..299 -> emit(midTaResponse.body()?.toDomain())
            else -> {
                Log.d("@@@@@@", "error ${midTaResponse.errorBody().toString()}")
            }
        }
    }

    override fun getVilageFcst(
        numOfRows: Int,
        pageNo: Int,
        nx: Int,
        ny: Int,
        baseDate: String,
        baseTime: String
    ): Flow<VilageFcstData?> = flow {

        val vilageFcstResponse = weatherApi.reqVilageFcst(
            serviceKey = BuildConfig.WEATHER_API_KEY,
            numOfRows = numOfRows,
            pageNo = pageNo,
            nX = nx,
            nY = ny,
            baseDate = baseDate,
            baseTime = baseTime,
            dataType = "JSON"
        )

        when(vilageFcstResponse.code()) {
            in 200..299 -> emit(vilageFcstResponse.body()?.toDomain())
            else -> {
                Log.d("@@@@@@", "error ${vilageFcstResponse.errorBody().toString()}")
            }
        }
    }

    override fun getUltraStrFcst(
        numOfRows: Int,
        pageNo: Int,
        nx: Int,
        ny: Int,
        baseDate: String,
        baseTime: String
    ): Flow<VilageFcstData?> = flow {

        val ultraStrFcstResponse = weatherApi.reqUltraStrFcst(
            serviceKey = BuildConfig.WEATHER_API_KEY,
            numOfRows = numOfRows,
            pageNo = pageNo,
            nX = nx,
            nY = ny,
            baseDate = baseDate,
            baseTime = baseTime,
            dataType = "JSON"
        )

        when(ultraStrFcstResponse.code()) {
            in 200..299 -> emit(ultraStrFcstResponse.body()?.toDomain())
            else -> {
                Log.d("@@@@@@", "error ${ultraStrFcstResponse.errorBody().toString()}")
            }
        }
    }


}