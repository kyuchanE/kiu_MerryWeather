package kiu.dev.merryweather.data.repository

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.BasicApi
import kiu.dev.merryweather.data.local.dao.WeatherMidDao
import kiu.dev.merryweather.data.local.dao.WeatherNowDao
import kiu.dev.merryweather.data.local.weather.mid.WeatherMid
import kiu.dev.merryweather.data.local.weather.now.WeatherNow
import kiu.dev.merryweather.di.NetworkModule
import kiu.dev.merryweather.utils.asJsonArray
import kiu.dev.merryweather.utils.asJsonObject
import kiu.dev.merryweather.utils.asString
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val baseApi: BasicApi,
    private val weatherNowDao: WeatherNowDao,
    private val weatherMidDao: WeatherMidDao
) {

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
    ): Flowable<JsonArray> {
        return baseApi.getApi(
            url = C.WeatherApi.WEATHER_NOW,
            params,
            header
        )
            .subscribeOn(Schedulers.io())
            .filter(::isWeatherSuccess)
            .flatMap(::getNowWeatherItemList)
            .filter(::filterNowWeather)
            .observeOn(AndroidSchedulers.mainThread())
    }

    // TODO chan Collection 함수 정리 필요!

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
    fun getRightNow(
        params: Map<String, Any?> = mapOf(),
        header: Map<String, Any?> = mapOf()
    ): Flowable<JsonObject> {
        return baseApi.getApi(
            url = C.WeatherApi.WEATHER_RIGHT_NOW,
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
    fun getWeatherMidTa(
        params: Map<String, Any?> = mapOf(),
        header: Map<String, Any?> = mapOf()
    ): Flowable<JsonObject> {
        return baseApi.getApi(
            url = C.WeatherApi.WEATHER_MID_TA,
            params,
            header
        )
    }

    /**
     * 기상청 중기 육상 예보 정보
     * @param ServiceKey  API KEY
     * @param dataType  JSON, XML
     * @param pageNo  페이지 번호
     * @param numOfRows  한 페이지 결과 수
     * @param regId  예보구역 코드 (11B10101 서울)
     * @param tmFc  발표시각 (일 2회 06:00 18:00 생성 YYYYMMDD0600(1800))
     */
    fun getWeatherMidFcst(
        params: Map<String, Any?> = mapOf(),
        header: Map<String, Any?> = mapOf()
    ): Flowable<JsonObject> {
        return baseApi.getApi(
            url = C.WeatherApi.WEATHER_MID_FCST,
            params,
            header
        )
    }

    /**
     * 로컬에 저장된 날씨 데이터 조회
     */
    fun getLocalWeatherData(): Flowable<List<WeatherNow>> =
        weatherNowDao.getWeatherData()

    /**
     * 로컬에 날씨 데이터 저장
     */
    fun saveLocalWeatherData(vararg weatherNow: WeatherNow): Completable =
        weatherNowDao.insertWeatherData(*weatherNow)

    /**
     * 로컬 날씨 데이터 삭제
     */
    fun deleteLocalWeatherData(data: WeatherNow): Completable =
        weatherNowDao.deleteWeatherData(data)


    /**
     * 로컬 날씨 데이터 삭제
     */
    fun deleteLocalWeatherData(vararg weatherNow: WeatherNow): Completable =
        weatherNowDao.deleteWeatherData(*weatherNow)


    /**
     * 로컬에 저장된 중기 날씨 데이터 조회
     */
    fun getLocalMidWeatherData(): Flowable<List<WeatherMid>> =
        weatherMidDao.getWeatherData()

    /**
     * 로컬에 중기 날씨 데이터 저장
     */
    fun saveLocalMidWeatherData(vararg weatherNow: WeatherMid): Completable =
        weatherMidDao.insertWeatherData(*weatherNow)

    /**
     * 로컬 날씨 데이터 삭제
     */
    fun deleteLocalMidWeatherData(data: WeatherMid): Completable =
        weatherMidDao.deleteWeatherData(data)


    /**
     * 로컬 날씨 데이터 삭제
     */
    fun deleteLocalMidWeatherData(vararg weatherNow: WeatherMid): Completable =
        weatherMidDao.deleteWeatherData(*weatherNow)

    /**
     *
     */
    private fun filterNowWeather(data: JsonArray): Boolean {

        return true
    }

    private fun getNowWeatherItemList(data: JsonObject): Flowable<JsonArray> =
        Flowable
            .fromArray(
                data.asJsonObject("response")
                    .asJsonObject("body")
                    .asJsonObject("items")
                    .asJsonArray("item")
            )
            .subscribeOn(Schedulers.io())
            .doOnError {

            }
            .doOnNext {

            }
            .doFinally {

            }

    /**
     * 기상청 Api ResultCode
     * @param data  json data
     * @return 00:정상, 01:어플리케이션 에러, 02:DB에러, 03:데이터 없음,
     * 04:HTTP에러, 05:서비스 연결 실패, 10:잘못된 요청 파라미터, 11:필수요청 에러,
     * 20:서비스 접근 거부, 21:사용할 수 없는 키, 22:서비스 요청제한 횟수 초과,
     * 30:등록되지 않은 키, 31:기한만료된 키, 32:등록되지 않은 IP, 33: 서명하지 않은 호출
     * 99:기타
     */
    private fun isWeatherSuccess(data: JsonObject): Boolean {
        val resultCode: String = try {
            data.asJsonObject("response")
                .asJsonObject("header")
                .asString("resultCode")
        } catch (e: Exception) {
            ""
        }

        if (resultCode == "00") {
            return true
        } else {
//            _showError.postValue(
//                data.asJsonObject("response")
//                    .asJsonObject("header")
//                    .asString("resultMsg")
//            )
            return false
        }
    }
}