package dev.kyu.data.api

import dev.kyu.data.model.MidLandFcstResponse
import dev.kyu.data.utils.NetworkConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET(NetworkConfig.Weather.Mid.midBaseUrl + NetworkConfig.Weather.Mid.midLandFcst)
    suspend fun reqMidLandFcst(
        @Query("serviceKey") serviceKey: String,        // 인증키
        @Query("numOfRows") numOfRows: Int,             // 한 페이지 결과 수
        @Query("pageNo") pageNo: Int,                   // 페이지 번호
        @Query("regId") regId: String,                  // 예보 구역 코드
        @Query("tmFc") tmFc: String,                    // 발표 시각
        @Query("dataType") dataType: String,            // XML/JSON
    ): Response<MidLandFcstResponse>

}