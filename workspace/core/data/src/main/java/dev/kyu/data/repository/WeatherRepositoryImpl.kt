package dev.kyu.data.repository

import android.util.Log
import dev.kyu.data.BuildConfig
import dev.kyu.data.api.WeatherApi
import dev.kyu.domain.model.MidLandFcstData
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

}