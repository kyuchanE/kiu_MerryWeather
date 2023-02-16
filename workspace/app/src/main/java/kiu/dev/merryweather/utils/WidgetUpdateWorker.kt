package kiu.dev.merryweather.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.data.local.widget.WidgetId
import kiu.dev.merryweather.data.repository.WeatherRepository
import kiu.dev.merryweather.ui.widget.SmallAppWidgetProvider
import javax.inject.Inject

class WidgetUpdateWorker @Inject constructor(
    context: Context,
    params: WorkerParameters,
    private val weatherRepository: WeatherRepository
): Worker(context, params) {
    private val widgetList = mutableListOf<WidgetId>()
    private var weathePasrams: Map<String, Any?> = mapOf()

    override fun doWork(): Result {

        getRightNowWeather()

        val outputData = workDataOf("public_key" to "android")
        return Result.success(outputData)
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

                    L.d("itemsJsonArray $itemsJsonArray")

                    val t1hList = arrayListOf<JsonElement>()
                    itemsJsonArray.forEach {
                        if (it.asJsonObject.asString("category") == "T1H") {
                            t1hList.add(it.asJsonObject)
                        }
                    }

                    val t = "${t1hList[0].asJsonObject.asString("fcstTime")} \n ${t1hList[0].asJsonObject.asString("fcstValue")}"

                    // TODO chan 데이터 가공하여 Widget Update
                    widgetList.forEach { id ->
                        SmallAppWidgetProvider.updateAppWidget(
                            appWidgetId = id.id?:0,
                            t = t,
                            s = ""
                        )
                    }

                }
            }
            .doFinally{

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
}