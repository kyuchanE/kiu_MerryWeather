package dev.kyu.data.api

import com.google.gson.JsonObject
import dev.kyu.data.model.MidLandFcstResponse
import dev.kyu.data.model.MidTaResponse
import dev.kyu.data.model.VilageFcstResponse
import dev.kyu.data.utils.NetworkConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    /**
     * 중기 육상 예보
     */
    @GET(NetworkConfig.Weather.Mid.midBaseUrl + NetworkConfig.Weather.Mid.midLandFcst)
    suspend fun reqMidLandFcst(
        @Query("serviceKey") serviceKey: String,        // 인증키
        @Query("numOfRows") numOfRows: Int,             // 한 페이지 결과 수
        @Query("pageNo") pageNo: Int,                   // 페이지 번호
        @Query("regId") regId: String,                  // 예보 구역 코드
        @Query("tmFc") tmFc: String,                    // 발표 시각
        @Query("dataType") dataType: String,            // XML/JSON
    ): Response<MidLandFcstResponse>

    /**
     * 중기 기온 조회
     */
    @GET(NetworkConfig.Weather.Mid.midBaseUrl + NetworkConfig.Weather.Mid.midTa)
    suspend fun reqMidTa(
        @Query("serviceKey") serviceKey: String,        // 인증키
        @Query("numOfRows") numOfRows: Int,             // 한 페이지 결과 수
        @Query("pageNo") pageNo: Int,                   // 페이지 번호
        @Query("regId") regId: String,                  // 예보 구역 코드
        @Query("tmFc") tmFc: String,                    // 발표 시각
        @Query("dataType") dataType: String,            // XML/JSON
    ): Response<MidTaResponse>

            /**
     * 초단기 예보 조회
     */
    @GET(NetworkConfig.Weather.Vilage.vilageBaseUrl + NetworkConfig.Weather.Vilage.ultraSrtFcst)
    suspend fun reqUltraStrFcst(
        @Query("serviceKey") serviceKey: String,        // 인증키
        @Query("numOfRows") numOfRows: Int,             // 한 페이지 결과 수
        @Query("pageNo") pageNo: Int,                   // 페이지 번호
        @Query("nx") nX: Int,                           // 예보 지점 X
        @Query("ny") nY: Int,                           // 예보 지점 Y
        @Query("base_date") baseDate: String,           // 발표 일자
        @Query("base_time") baseTime: String,           // 발표 시각
        @Query("dataType") dataType: String,            // XML/JSON
    ): Response<VilageFcstResponse>

    /**
     * 단기 예보 조회
     */
    @GET(NetworkConfig.Weather.Vilage.vilageBaseUrl + NetworkConfig.Weather.Vilage.vilageFcst)
    suspend fun reqVilageFcst(
        @Query("serviceKey") serviceKey: String,        // 인증키
        @Query("numOfRows") numOfRows: Int,             // 한 페이지 결과 수
        @Query("pageNo") pageNo: Int,                   // 페이지 번호
        @Query("nx") nX: Int,                           // 예보 지점 X
        @Query("ny") nY: Int,                           // 예보 지점 Y
        @Query("base_date") baseDate: String,           // 발표 일자
        @Query("base_time") baseTime: String,           // 발표 시각
        @Query("dataType") dataType: String,            // XML/JSON
    ): Response<VilageFcstResponse>

}