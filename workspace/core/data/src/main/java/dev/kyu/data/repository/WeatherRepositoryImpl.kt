package dev.kyu.data.repository

import android.util.Log
import dev.kyu.data.BuildConfig
import dev.kyu.data.api.WeatherApi
import dev.kyu.domain.model.MidLandFcstData
import dev.kyu.domain.model.VilageFcstData
import dev.kyu.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi
): WeatherRepository {

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