package kiu.dev.merryweather.utils

import android.content.Context
import androidx.room.Room
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.BasicApi
import kiu.dev.merryweather.data.local.widget.WidgetId
import kiu.dev.merryweather.data.local.widget.WidgetIdDataBase
import kiu.dev.merryweather.data.repository.WeatherRepository
import kiu.dev.merryweather.data.repository.WidgetIdRepository
import kiu.dev.merryweather.di.NetworkModule
import kiu.dev.merryweather.ui.widget.SmallAppWidgetProvider
import javax.inject.Inject

class WidgetUpdateWorker(
    val context: Context,
    params: WorkerParameters
): Worker(context, params) {
    private val widgetList = mutableListOf<WidgetId>()

    override fun doWork(): Result {
        L.d("WidgetUpdateWorker doWork")

        getWidgetId()
//        val outputData = workDataOf("public_key" to "android")
        return Result.success()
    }

    private fun getWidgetId() {
        Room.databaseBuilder(
            context,
            WidgetIdDataBase::class.java,
            C.RoomTableName.WIDGET_ID
        )
            .fallbackToDestructiveMigration()
            .build()
            .widgetIdDAO()
            .getWidgetId()
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

                getWeatherData()
            }
            .subscribe()
    }

    private fun getWeatherData() {
        var nx: String = C.WeatherData.Location.Seoul["nx"] ?: ""
        var ny: String = C.WeatherData.Location.Seoul["ny"] ?: ""

        var nowDate: String = "YYYYMMdd".getTimeNow()
        val nowTimeHour: Int = "HH".getTimeNow().toInt()
        val nowTimeMinute: Int = "mm".getTimeNow().toInt()

        L.d("reqWeatherUltraNow : $nowDate , hour : $nowTimeHour , minute : $nowTimeMinute")

        val baseTime: String = if (nowTimeMinute >= 30){
            String.format("%02d", nowTimeHour) + String.format("%02d", nowTimeMinute)
        } else {
            if (nowTimeHour == 0) {
                nowDate = "YYYYMMdd".getYesterday()
                "2330"
            } else {
                String.format("%02d", nowTimeHour-1) + "55"
            }
        }

        NetworkModule.provideRetrofit(
            NetworkModule.provideOkHttpClient(
                NetworkModule.httpLoggingInterceptor()
            )
        ).create(BasicApi::class.java)
            .getApi(
                url = C.WeatherApi.WEATHER_RIGHT_NOW,
                params = mapOf(
                    "ServiceKey" to C.WeatherApi.API_KEY,
                    "dataType" to "JSON",
                    "pageNo" to "1",
                    "numOfRows" to "50",
                    "base_date" to nowDate,
                    "base_time" to baseTime,
                    "nx" to nx,
                    "ny" to ny
                )
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

                    val t = "Worker ${t1hList[0].asJsonObject.asString("fcstTime")} \n ${t1hList[0].asJsonObject.asString("fcstValue")}"

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
            .doFinally{ }
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

            return false
        }
    }
}