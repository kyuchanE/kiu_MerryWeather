package kiu.dev.merryweather.ui.fragment

import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.gson.JsonElement
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.base.BaseFragment
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.local.Weather
import kiu.dev.merryweather.data.local.WidgetId
import kiu.dev.merryweather.databinding.FragmentWeatherBinding
import kiu.dev.merryweather.ui.activity.MainViewModel
import kiu.dev.merryweather.ui.widget.SmallAppWidgetProvider
import kiu.dev.merryweather.utils.L
import kiu.dev.merryweather.utils.asString
import kiu.dev.merryweather.utils.getTimeNow
import kiu.dev.merryweather.utils.toast

class WeatherFragment: BaseFragment<FragmentWeatherBinding>() {
    override val layoutId: Int = R.layout.fragment_weather

    private val viewModel by activityViewModels<MainViewModel>()
    private val widgetIdList = mutableListOf<WidgetId>()
    private val localWeatherDataList = mutableListOf<Weather>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()

        binding.tvNow.setOnClickListener {
            reqWeatherUltraNow(
                C.WeatherData.Location.Seoul["nx"]?:"",
                C.WeatherData.Location.Seoul["ny"]?:""
            )

//            reqWeatherNow(
//                C.WeatherData.Location.Seoul["nx"]?:"",
//                C.WeatherData.Location.Seoul["ny"]?:""
//            )

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
                    "nx" to C.WeatherData.Location.Seoul["nx"],
                    "ny" to C.WeatherData.Location.Seoul["ny"]
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getWidgetId()
        viewModel.getLocalWeatherData()
    }

    /**
     * 기상청 초단기 예보 조회
     */
    private fun reqWeatherUltraNow(nx: String, ny: String) {
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
        baseTime.toast((activity as BaseActivity<*>))

        viewModel.getUltraNow(
            mapOf(
                "ServiceKey" to C.WeatherApi.API_KEY,
                "dataType" to "JSON",
                "pageNo" to "1",
                "numOfRows" to "50",
                "base_date" to nowDate,
                "base_time" to baseTime,
                "nx" to nx,
                "ny" to ny
            )
        )
    }

    /**
     * 기상청 단기 예보 조회
     * 발표 시각 : 0210, 0510, 0810, 1110, 1410, 1710, 2010, 2310
     */
    private fun reqWeatherNow(nx: String, ny: String) {
        val nowDate: String = "YYYYMMdd".getTimeNow()
        val nowHour: String = "HH".getTimeNow()
        val nowTime: String = "HHmm".getTimeNow()
        var baseTime = ""



        kotlin.run {
            C.WeatherData.WEATHER_NOW_GET_DATA_TIME.forEachIndexed { index, item ->
                L.d("reqWeatherNow nowTime : ${nowTime.toInt()}  , item : ${item.toInt()}")
                if (nowHour == "00" || nowHour == "01") {
                    baseTime = "2310"
                    return@run
                } else if (nowTime.toInt() in 200..210){
                    baseTime = "2310"
                    return@run
                } else if (item.toInt() > nowTime.toInt()) {
                    baseTime = C.WeatherData.WEATHER_NOW_GET_DATA_TIME[index-1]
                    L.d("@@@@@@@ baseTime : $baseTime")
                    return@run
                } else {
                    baseTime = nowTime
                }
            }
        }


        baseTime.toast((activity as BaseActivity<*>))

        viewModel.getNow(
            mapOf(
                "ServiceKey" to C.WeatherApi.API_KEY,
                "dataType" to "JSON",
                "pageNo" to "1",
                "numOfRows" to "1000",
                "base_date" to nowDate,
                "base_time" to baseTime,
                "nx" to nx,
                "ny" to ny
            )
        )
    }


    /**
     * viewModel observe 세팅
     */
    private fun initViewModel() {

        with(viewModel) {

            isLoading.observe((activity as BaseActivity<*>)) {
                if (it){
                    (activity as BaseActivity<*>).showLoading()
                    return@observe
                }
                (activity as BaseActivity<*>).hideLoading()
            }

            showError.observe((activity as BaseActivity<*>)) {
                it.toast((activity as BaseActivity<*>))
            }

            weatherNowJson.observe((activity as BaseActivity<*>)) {
                L.d("weatherNowJson observe : $it")

                var tpm = ""
                it.forEach {
                    if (it.asJsonObject.asString("category") == "T1H") {
                        tpm += "time : ${it.asJsonObject.asString("fcstTime")} value : ${it.asJsonObject.asString("fcstValue")} \n"
                    }
                }

                binding.tvValue.text = tpm
            }

            weatherUltraNowJson.observe((activity as BaseActivity<*>)) {
                L.d("weatherUltraNowJson observe : $it")
                "Ultra Success".toast((activity as BaseActivity<*>))

                val t1hList = arrayListOf<JsonElement>()

                // TODO chan 임시 날씨 데이터 show
                var tpm = ""
                it.forEach {
                    if (it.asJsonObject.asString("category") == "T1H") {
                        t1hList.add(it.asJsonObject)
                        tpm += "time : ${it.asJsonObject.asString("fcstTime")} value : ${it.asJsonObject.asString("fcstValue")} \n"
                    }
                }
                binding.tvValue.text = tpm

                // 위젯 데이터 갱신
                val t = "${t1hList[0].asJsonObject.asString("fcstTime")} \n ${t1hList[0].asJsonObject.asString("fcstValue")}"
                L.d("@@@@@@@@@ t : $t")

                this@WeatherFragment.widgetIdList.forEach {
                    it.id?.let {
                        SmallAppWidgetProvider.updateAppWidget(
                            (activity as BaseActivity<*>).context,
                            AppWidgetManager.getInstance((activity as BaseActivity<*>).context),
                            it,
                            t
                        )
                    }
                }

                saveLocalWeatherData(it)

            }

            weatherWeekJson.observe((activity as BaseActivity<*>)) {
                L.d("weatherWeekJson observe : $it")
            }

            widgetIdList.observe((activity as BaseActivity<*>)) {
                L.d("widgetIdList observe : $it")
                this@WeatherFragment.widgetIdList.clear()
                it.forEach { widgetId ->
                    this@WeatherFragment.widgetIdList.add(widgetId)
                }
                L.d("fragment widgetIdList : ${this@WeatherFragment.widgetIdList}")
            }

            localWeatherDataList.observe((activity as BaseActivity<*>)) {
                L.d("localWeatherData observe : $it")
                this@WeatherFragment.localWeatherDataList.clear()
                this@WeatherFragment.localWeatherDataList.addAll(it)

            }
        }

    }

    /**
     * ROOM 로컬 날씨 데이터 저장
     */
    private fun saveLocalWeatherData(data: List<JsonElement>) {
        // TODO chan 중복된 시간 데이터는 갱신 / 새로운 데이터는 추가,
        //  초단기, 단기 중기 데이터 꺼내오는 로직 분기처리 필요


        // TODO chan 임시로 초단기 데이터 테스트
        val dataList: MutableList<Weather> = mutableListOf()

        // 시간 값 세팅
        val timeList: MutableList<Map<String, String>> = mutableListOf()
        data.forEach {
            val timeItem = mapOf(
                "fcstDate" to
                        it.asJsonObject.asString("fcstDate"),
                "fcstTime" to
                        it.asJsonObject.asString("fcstTime")
            )

            if (!timeList.contains(timeItem)) {
                timeList.add(timeItem)
            }
        }

        timeList.forEach { timeData ->
            var tmp = ""
            var sky = ""
            var pty = ""
            var pcp = ""

            data.forEach { data ->
                if (data.asJsonObject.asString("fcstDate") == timeData["fcstDate"] &&
                    data.asJsonObject.asString("fcstTime") == timeData["fcstTime"]) {

                    if (data.asJsonObject.asString("category") == "PTY") {
                        pty = data.asJsonObject.asString("fcstValue")
                    } else if (data.asJsonObject.asString("category") == "T1H") {
                        tmp = data.asJsonObject.asString("fcstValue")
                    } else if (data.asJsonObject.asString("category") == "SKY") {
                        sky = data.asJsonObject.asString("fcstValue")
                    } else if (data.asJsonObject.asString("category") == "RN1") {
                        pcp = data.asJsonObject.asString("fcstValue")
                    }
                }

            }

            dataList.add(
                Weather(
                    time = (timeData["fcstDate"] + timeData["fcstTime"]).toLong(),
                    tmp = tmp,
                    sky = sky,
                    pty = pty,
                    pcp = pcp
                )
            )
        }

        viewModel.saveLocalWeatherData(dataList)
    }
}