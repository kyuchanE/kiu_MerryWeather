package kiu.dev.merryweather.ui

import android.appwidget.AppWidgetManager
import android.os.Bundle
import com.google.gson.JsonElement
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.databinding.ActivityMainBinding
import kiu.dev.merryweather.ui.widget.SmallAppWidgetProvider
import kiu.dev.merryweather.utils.L
import kiu.dev.merryweather.utils.asString
import kiu.dev.merryweather.utils.getTimeNow
import kiu.dev.merryweather.utils.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId: Int = R.layout.activity_main
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()

        binding.tvNow.setOnClickListener {
            reqWeatherUltraNow()
//            reqWeatherNow()

        }

        binding.tvWeek.setOnClickListener {
            val nowDate: String = "YYYYMMdd".getTimeNow()
            val nowTime: String = "HHmm".getTimeNow()
            L.d("Now date : $nowDate , time : $nowTime")
            viewModel.getWeek(
                mapOf(
                    "ServiceKey" to C.WeatherApi.API_KEY,
                    "dataType" to "JSON",
                    "pageNo" to "1",
                    "numOfRows" to "50",
                    "base_date" to nowDate,
                    "base_time" to "0500",
                    "nx" to "60",
                    "ny" to "127"
                )
            )
        }

    }

    /**
     * 기상청 초단기 예보 조회
     */
    private fun reqWeatherUltraNow() {
        val nowDate: String = "YYYYMMdd".getTimeNow()
        val nowTimeHour: Int = "HH".getTimeNow().toInt()
        val nowTimeMinute: Int = "mm".getTimeNow().toInt()

        L.d("reqWeatherUltraNow : $nowDate , hour : $nowTimeHour , minute : $nowTimeMinute")

        val baseTime: String = if (nowTimeMinute >= 30){
            String.format("%02d", nowTimeHour) + String.format("%02d", nowTimeMinute)
        } else {
            if (nowTimeHour == 0) {
                "2330"
            } else {
                String.format("%02d", nowTimeHour-1) + "55"
            }
        }
        baseTime.toast(this)

        viewModel.getUltraNow(
            mapOf(
                "ServiceKey" to C.WeatherApi.API_KEY,
                "dataType" to "JSON",
                "pageNo" to "1",
                "numOfRows" to "50",
                "base_date" to nowDate,
                "base_time" to baseTime,
                "nx" to "60",
                "ny" to "127"
            )
        )
    }

    /**
     * 기상청 단기 예보 조회
     * 발표 시각 : 0210, 0510, 0810, 1110, 1410, 1710, 2010, 2310
     */
    private fun reqWeatherNow() {
        val nowDate: String = "YYYYMMdd".getTimeNow()
        val nowHour: String = "HH".getTimeNow()
        val nowTime: String = "HHmm".getTimeNow()
        var baseTime = ""



        kotlin.run {
            C.WEATHER_NOW_GET_DATA_TIME.forEachIndexed { index, item ->
                L.d("reqWeatherNow nowTime : ${nowTime.toInt()}  , item : ${item.toInt()}")
                if (nowHour == "00" || nowHour == "01") {
                    baseTime = "2310"
                    return@run
                } else if (nowTime.toInt() in 200..210){
                    baseTime = "2310"
                    return@run
                } else if (item.toInt() > nowTime.toInt()) {
                    baseTime = C.WEATHER_NOW_GET_DATA_TIME[index-1]
                    L.d("@@@@@@@ baseTime : $baseTime")
                    return@run
                } else {
                    baseTime = nowTime
                }
            }
        }


        baseTime.toast(this)

        viewModel.getNow(
            mapOf(
                "ServiceKey" to C.WeatherApi.API_KEY,
                "dataType" to "JSON",
                "pageNo" to "1",
                "numOfRows" to "1000",
                "base_date" to nowDate,
                "base_time" to baseTime,
                "nx" to "60",
                "ny" to "127"
            )
        )
    }

    /**
     * viewModel observe 세팅
     */
    private fun initViewModel() {

        with(viewModel) {

            isLoading.observe(this@MainActivity) {
                if (it){
                    showLoading()
                    return@observe
                }
                hideLoading()
            }

            showError.observe(this@MainActivity) {
                it.toast(this@MainActivity)
            }

            weatherNowJson.observe(this@MainActivity) {
                L.d("weatherNowJson observe : $it")

                var tpm = ""
                it.forEach {
                    if (it.asJsonObject.asString("category") == "T1H") {
                        tpm += "time : ${it.asJsonObject.asString("fcstTime")} value : ${it.asJsonObject.asString("fcstValue")} \n"
                    }
                }

                binding.tvValue.text = tpm
            }

            weatherUltraNowJson.observe(this@MainActivity) {
                L.d("weatherUltraNowJson observe : $it")
                "Ultra Success".toast(this@MainActivity)

                val t1hList = arrayListOf<JsonElement>()

                var tpm = ""
                it.forEach {
                    if (it.asJsonObject.asString("category") == "T1H") {
                        t1hList.add(it.asJsonObject)
                        tpm += "time : ${it.asJsonObject.asString("fcstTime")} value : ${it.asJsonObject.asString("fcstValue")} \n"
                    }
                }



                val t = "${t1hList[0].asJsonObject.asString("fcstTime")} \n ${t1hList[0].asJsonObject.asString("fcstValue")}"
                L.d("@@@@@@@@@ t : $t")
                SmallAppWidgetProvider.updateAppWidget(
                    this@MainActivity,
                    AppWidgetManager.getInstance(this@MainActivity),
                    42,
                    t
                )


                binding.tvValue.text = tpm

            }

            weatherWeekJson.observe(this@MainActivity) {
                L.d("weatherWeekJson observe : $it")
            }
        }

    }

}