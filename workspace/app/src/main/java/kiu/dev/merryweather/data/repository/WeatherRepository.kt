package kiu.dev.merryweather.data.repository

import com.google.gson.JsonObject
import io.reactivex.Flowable
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.BasicApi
import org.koin.core.component.KoinComponent

class WeatherRepository (
    private val baseApi: BasicApi
): KoinComponent {

    /**
     * 기상청 단기 예보 정보 (1일 8회)
     * @param ServiceKey  API KEY
     * @param dataType  JSON, XML
     * @param pageNo  페이지 번호
     * @param numOfRows  한 페이지 결과 수
     * @param base_date  발표일자
     * @param base_time  발표시각  (0210, 0510, 0810, 1110, 1410, 1710, 2010, 2310)
     * @param nx : 예보지점 X 좌표
     * @param ny : 예보지점 Y 좌표
     */
    fun getNow(
        params: Map<String, Any?> = mapOf(),
        header: Map<String, Any?> = mapOf()
    ): Flowable<JsonObject> {
        return baseApi.getApi(
            url = C.WeatherApi.WEATHER_NOW,
            params,
            header
        )
    }

    /**
     * 기상청 초단기 예보 정보
     * @param ServiceKey  API KEY
     * @param dataType  JSON, XML
     * @param pageNo  페이지 번호
     * @param numOfRows  한 페이지 결과 수
     * @param base_date  발표일자
     * @param base_time  발표시각
     * @param nx  예보지점 X 좌표
     * @param ny  예보지점 Y 좌표
     */
    fun getUltraNow(
        params: Map<String, Any?> = mapOf(),
        header: Map<String, Any?> = mapOf()
    ): Flowable<JsonObject> {
        return baseApi.getApi(
            url = C.WeatherApi.WEATHER_ULTRA_NOW,
            params,
            header
        )
    }

    /**
     * 기상청 중기 기온 정보
     * @param ServiceKey  API KEY
     * @param dataType  JSON, XML
     * @param pageNo  페이지 번호
     * @param numOfRows  한 페이지 결과 수
     * @param regId  예보구역 코드 (11B10101 서울)
     * @param tmFc  발표시각 (일 2회 06:00 18:00 생성 YYYYMMDD0600(1800))
     */
    fun getWeek(
        params: Map<String, Any?> = mapOf(),
        header: Map<String, Any?> = mapOf()
    ): Flowable<JsonObject> {
        return baseApi.getApi(
            url = C.WeatherApi.WEATHER_WEEK,
            params,
            header
        )
    }

}