package kiu.dev.merryweather.ui.fragment

import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonElement
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.base.BaseFragment
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.WeatherTimeLineData
import kiu.dev.merryweather.data.local.Weather
import kiu.dev.merryweather.data.local.WidgetId
import kiu.dev.merryweather.databinding.FragmentWeatherBinding
import kiu.dev.merryweather.ui.activity.MainViewModel
import kiu.dev.merryweather.ui.fragment.adapter.WeatherTimeLineAdapter
import kiu.dev.merryweather.ui.widget.SmallAppWidgetProvider
import kiu.dev.merryweather.utils.*

class WeatherFragment: BaseFragment<FragmentWeatherBinding>() {
    override val layoutId: Int = R.layout.fragment_weather

    private val viewModel by activityViewModels<MainViewModel>()
    private val widgetIdList = mutableListOf<WidgetId>()
    private val localWeatherDataList = mutableListOf<Weather>()
    private val weatherTimeLineAdapter = lazy { WeatherTimeLineAdapter(mutableListOf()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        reqWeatherData()

        with(binding.srlContainer) {
            this.setOnRefreshListener {
                reqWeatherData()
                this.isRefreshing = false
            }
        }

        binding.rvTimeLine.apply {
            this.adapter = weatherTimeLineAdapter.value
            this.layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }

        // TODO chan TEST DATA
        weatherTimeLineAdapter.value.changeItemList(
            mutableListOf(
                WeatherTimeLineData(
                    "Date",
                    "11:32",
                    activity?.getDrawable(R.drawable.icon_sunny)!!,
                    "4"),
                WeatherTimeLineData(
                    "Date",
                    "11:32",
                    activity?.getDrawable(R.drawable.icon_sunny)!!,
                    "4"),
                WeatherTimeLineData(
                    "Date",
                    "11:32",
                    activity?.getDrawable(R.drawable.icon_sunny)!!,
                    "4"),
                WeatherTimeLineData(
                    "Date",
                    "11:32",
                    activity?.getDrawable(R.drawable.icon_sunny)!!,
                    "4"),
                WeatherTimeLineData(
                    "Date",
                    "11:32",
                    activity?.getDrawable(R.drawable.icon_sunny)!!,
                    "4"),
                WeatherTimeLineData(
                    "Date",
                    "11:32",
                    activity?.getDrawable(R.drawable.icon_sunny)!!,
                    "4"),
                WeatherTimeLineData(
                    "Date",
                    "11:32",
                    activity?.getDrawable(R.drawable.icon_sunny)!!,
                    "4"),
                WeatherTimeLineData(
                    "Date",
                    "11:32",
                    activity?.getDrawable(R.drawable.icon_sunny)!!,
                    "4"),
                WeatherTimeLineData(
                    "Date",
                    "11:32",
                    activity?.getDrawable(R.drawable.icon_sunny)!!,
                    "4"),
                WeatherTimeLineData(
                    "Date",
                    "11:32",
                    activity?.getDrawable(R.drawable.icon_sunny)!!,
                    "4"),
                WeatherTimeLineData(
                    "Date",
                    "11:32",
                    activity?.getDrawable(R.drawable.icon_sunny)!!,
                    "4")
            )
        )

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
     * 날씨 데이터 요청
     */
    private fun reqWeatherData()
    {
        reqWeatherNow(
            C.WeatherData.Location.Seoul["nx"] ?: "",
            C.WeatherData.Location.Seoul["ny"] ?: ""
        )
    }

    /**
     * 기상청 초단기 예보 조회
     */
    private fun reqWeatherUltraNow(nx: String, ny: String) {
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
        baseTime.toast((activity as BaseActivity<*>))

        viewModel.getRightNowWeather(
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
        var nowDate: String = "YYYYMMdd".getTimeNow()
        val nowHour: String = "HH".getTimeNow()
        val nowTime: String = "HHmm".getTimeNow()
        var baseTime = ""

        kotlin.run {
            C.WeatherData.WEATHER_NOW_GET_DATA_TIME.forEachIndexed { index, item ->
                L.d("reqWeatherNow nowTime : ${nowTime.toInt()}  , item : ${item.toInt()}")
                if (nowHour == "00" || nowHour == "01") {
                    nowDate = "YYYYMMdd".getYesterday()
                    baseTime = "2310"
                    return@run
                } else if (nowTime.toInt() in 200..210){
                    nowDate = "YYYYMMdd".getYesterday()
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

        viewModel.getNowWeather(
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

//                var tpm = ""
//                it.forEach {
//                    if (it.asJsonObject.asString("category") == "T1H") {
//                        tpm += "time : ${it.asJsonObject.asString("fcstTime")} value : ${it.asJsonObject.asString("fcstValue")} \n"
//                    }
//                }
//
//                binding.tvValue.text = tpm


            }

            weatherRightNowJson.observe((activity as BaseActivity<*>)) {
                L.d("weatherRightNowJson observe : $it")
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

                val t = "${t1hList[0].asJsonObject.asString("fcstTime")} \n ${t1hList[0].asJsonObject.asString("fcstValue")}"
                L.d("@@@@@@@@@ t : $t")

                // 위젯 데이터 갱신
                this@WeatherFragment.widgetIdList.forEach {
                    it.id?.let {
                        SmallAppWidgetProvider.updateAppWidget(
                            (activity as BaseActivity<*>).context,
                            AppWidgetManager.getInstance((activity as BaseActivity<*>).context),
                            it,
                            t,
                            ""
                        )
                    }
                }

                viewModel.saveLocalWeatherData(it, MainViewModel.WeatherType.RIGHT_NOW)

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
                deleteBeforeLocalWeatherData(this@WeatherFragment.localWeatherDataList)
            }
        }

    }

}