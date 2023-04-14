package kiu.dev.merryweather.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.local.widget.WidgetId
import kiu.dev.merryweather.data.repository.AirRepository
import kiu.dev.merryweather.data.repository.WeatherRepository
import kiu.dev.merryweather.data.repository.WidgetIdRepository
import kiu.dev.merryweather.di.NetworkModule
import kiu.dev.merryweather.ui.widget.SmallAppWidgetProvider
import kiu.dev.merryweather.utils.*
import org.json.JSONObject
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class WidgetViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val widgetIdRepository: WidgetIdRepository,
    private val airRepository: AirRepository
) {

    private val widgetList = mutableListOf<WidgetId>()
    private var weathePasrams: Map<String, Any?> = mapOf()

    private var tmp = ""
    private var pty = ""
    private var sky = ""

    val cityAirList = mutableListOf<JSONObject>()

    enum class WeatherType {
        NOW, // 단기 예보
        RIGHT_NOW, // 초단기 예보
        MID
    }

    /**
     * 기상청 단기 예보 정보 (1일 8회) + 끝나고 초단기 예보 조회
     * @param ServiceKey  API KEY
     * @param dataType  JSON, XML
     * @param pageNo : 페이지 번호
     * @param numOfRows  한 페이지 결과 수
     * @param base_date  발표일자
     * @param base_time  발표시각 (0210, 0510, 0810, 1110, 1410, 1710, 2010, 2310)
     * @param nx  예보지점 X 좌표
     * @param ny  예보지점 Y 좌표
     */
    fun getNowWeather(
        params: Map<String, Any?> = mapOf()
    ) {
        // TODO 강수확률(POP), 일 최저기온(TMN), 일 최고기온(TMX), 오늘 시간별 날씨 정보
        //  base_tim 발표 시각 계산 필요 및 이전 예보 저장 후 사용
        //  (이미 존재하는 값이면 API 조회 안함)
        //  일 최고/최저 온도 미리 저장하여 사용

//        weatherRepository.getNow(
//            params = params
//        ).subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnError { e ->
//                L.d("e : $e")
//            }
//            .doOnNext { json ->
//                L.d("json : $json")
//                val itemsJsonArray: List<JsonElement> =
//                    json.filter {
//                        it.asJsonObject.asString("category") == "POP" ||    // 강수 확률
//                                it.asJsonObject.asString("category") == "PCP" || // 1시간 강수량
//                                it.asJsonObject.asString("category") == "TMP" || // 1시간 기온
//                                it.asJsonObject.asString("category") == "SKY" || // 하늘
//                                it.asJsonObject.asString("category") == "TMN" || // 일 최저 기온
//                                it.asJsonObject.asString("category") == "TMX" || // 일 최고 기온
//                                it.asJsonObject.asString("category") == "PTY"   // 강수 형태
//                    }
//                _weatherNowJson.postValue(itemsJsonArray)
//
//            }
//            .doFinally{
//                var nowDate: String = "YYYYMMdd".getTimeNow()
//                val nowTimeHour: Int = "HH".getTimeNow().toInt()
//                val nowTimeMinute: Int = "mm".getTimeNow().toInt()
//
//                L.d("reqWeatherRightNow : $nowDate , hour : $nowTimeHour , minute : $nowTimeMinute")
//
//                val baseTime: String = if (nowTimeMinute >= 30){
//                    String.format("%02d", nowTimeHour) + String.format("%02d", nowTimeMinute)
//                } else {
//                    if (nowTimeHour == 0) {
//                        nowDate = "YYYYMMdd".getYesterday()
//                        "2330"
//                    } else {
//                        String.format("%02d", nowTimeHour-1) + "55"
//                    }
//                }
//
//                getRightNowWeather(
//                    mapOf(
//                        "ServiceKey" to C.WeatherApi.API_KEY,
//                        "dataType" to "JSON",
//                        "pageNo" to "1",
//                        "numOfRows" to "50",
//                        "base_date" to nowDate,
//                        "base_time" to baseTime,
//                        "nx" to params["nx"],
//                        "ny" to params["ny"]
//                    )
//                )
//            }
//            .subscribe()

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
    private fun getRightNowWeather() {
        weatherRepository.getRightNow(
            params = weathePasrams
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e ->
                L.d("e : $e")
            }
            .doOnNext { json ->
                L.d("json : $json")

                if (isWeatherSuccess(json)){
                    val itemsJsonArray: List<JsonElement> =
                        json.asJsonObject("response")
                            .asJsonObject("body")
                            .asJsonObject("items")
                            .asJsonArray("item")
                            .filter {
                                it.asJsonObject.asString("category") == "T1H" ||    // 기온
                                        it.asJsonObject.asString("category") == "RN1" ||    // 1시간 강수량
                                        it.asJsonObject.asString("category") == "SKY" ||    // 하늘 상태
                                        it.asJsonObject.asString("category") == "PTY"   // 강수형태
                            }

                    kotlin.run {
                        itemsJsonArray.forEach {
                            if (it.asJsonObject.asString("category") == "T1H") {
                                tmp = it.asJsonObject.asString("fcstValue")
                                return@run
                            }
                        }
                    }

                    kotlin.run {
                        itemsJsonArray.forEach {
                            if (it.asJsonObject.asString("category") == "SKY") {
                                sky = it.asJsonObject.asString("fcstValue")
                                return@run
                            }
                        }
                    }

                    kotlin.run {
                        itemsJsonArray.forEach {
                            if (it.asJsonObject.asString("category") == "PTY") {
                                pty = it.asJsonObject.asString("fcstValue")
                                return@run
                            }
                        }
                    }

                    getCityAir(
                        mapOf(
                            "serviceKey" to C.WeatherApi.API_KEY,
                            "returnType" to "json",
                            "sidoName" to "서울",
                            "ver" to 1.3
                        )
                    )

                }
            }
            .doFinally{

            }
            .subscribe()

    }

    /**
     * 위젯 아이디 조회
     */
    private fun getWidgetId() {
        widgetIdRepository.getWidgetId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e ->
                L.d("e : $e")
            }
            .doOnNext { list ->
                L.d("list : $list")
                widgetList.clear()
                list.forEach {
                    widgetList.add(it)
                }

                getRightNowWeather()
            }
            .subscribe()
    }

    /**
     * 위젯 아이디 저장
     */
    fun saveWidgetId(vararg id: WidgetId) {
        L.d("WidgetViewModel saveWidgetId")
        widgetIdRepository.saveWidgetId(*id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e ->

            }
            .subscribe()
    }

    fun updateWeatherData(params: Map<String, Any?> = mapOf()) {
        L.d("WidgetViewModel updateWeatherData")
        weathePasrams = params
        getWidgetId()
    }

    /**
     * 위젯 아이디 삭제
     */
    fun deleteWidgetId(id: WidgetId) {
        L.d("WidgetViewModel deleteWidgetId id : $id")
        widgetIdRepository.deleteWidgetId(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e ->

            }
            .subscribe()
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
            // TODO chan 통신 오류 발생
//            _showError.postValue(
//                data.asJsonObject("response")
//                    .asJsonObject("header")
//                    .asString("resultMsg")
//            )
            return false
        }
    }

    private fun getCityAir(
        params: Map<String, Any?> = mapOf()
    ) {
        airRepository.getCityAir(
            params = params
        )
            .doOnError { e ->

            }
            .doOnNext { jsonArray ->
                cityAirList.clear()
                jsonArray.forEach { json ->
                    val data = JSONObject()
                    json as JsonObject
                    data.put("pm10Value", json.asString("pm10Value", ""))
                    data.put("pm10Grade1h", json.asString("pm10Grade1h", ""))
                    data.put("pm25Value", json.asString("pm25Value", ""))
                    data.put("pm25Grade1h", json.asString("pm25Grade1h", ""))
                    data.put("dataTime", json.asString("dataTime", ""))
                    cityAirList.add(data)
                }
            }
            .doFinally {
                var airStr = ""

                if (cityAirList.size > 0) {
                    val pm10Grade: String =
                        when(cityAirList[0].getString("pm10Grade1h")) {
                            "1" -> "좋음"
                            "2" -> "보통"
                            "3" -> "나쁨"
                            "4" -> "매우나쁨"
                            else -> ""
                        }

                    val pm25Grade: String =
                        when (cityAirList[0].getString("pm25Grade1h")) {
                            "1" -> "좋음"
                            "2" -> "보통"
                            "3" -> "나쁨"
                            "4" -> "매우나쁨"
                            else -> ""
                        }
                    airStr = "미세먼지 : " +
                            "${cityAirList[0].getString("pm10Value")} , $pm10Grade" +
                            "   초미세먼지 : " +
                            "${cityAirList[0].getString("pm25Value")} , $pm25Grade"

                }

                // TODO chan 데이터 가공하여 Widget Update
                widgetList.forEach { id ->
                    SmallAppWidgetProvider.updateAppWidget(
                        appWidgetId = id.id?:0,
                        tmp = tmp,
                        sky = sky,
                        pty = pty,
                        air = airStr
                    )
                }
            }
            .subscribe()

    }
}