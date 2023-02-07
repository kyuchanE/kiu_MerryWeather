package kiu.dev.merryweather.ui.fragment

import android.appwidget.AppWidgetManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.gson.JsonElement
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.base.BaseFragment
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.local.WeatherTimeLineData
import kiu.dev.merryweather.data.local.weather.now.WeatherNow
import kiu.dev.merryweather.data.local.widget.WidgetId
import kiu.dev.merryweather.databinding.FragmentWeatherBinding
import kiu.dev.merryweather.ui.adapter.WeatherTimeLineAdapter
import kiu.dev.merryweather.ui.viewmodel.MainViewModel
import kiu.dev.merryweather.ui.widget.SmallAppWidgetProvider
import kiu.dev.merryweather.utils.*

class WeatherFragment: BaseFragment<FragmentWeatherBinding>() {
    override val layoutId: Int = R.layout.fragment_weather

    private val viewModel by activityViewModels<MainViewModel>()
    private val widgetIdList = mutableListOf<WidgetId>()
    private val localWeatherNowDataList = mutableListOf<WeatherNow>()
    private val weatherTimeLineAdapter = lazy { WeatherTimeLineAdapter(mutableListOf()) }

    companion object {
        const val KEY = "key"
        fun newInstance(): WeatherFragment = WeatherFragment().apply {
            arguments = Bundle().apply {
                val args = Bundle()
//                args.putString(KEY, data)
                arguments = args

            }
        }
    }

    // TODO chan Fragment 활용법


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initUI()
        reqWeatherData()

    }

    override fun onResume() {
        super.onResume()

        viewModel.getWidgetId()
        viewModel.getLocalWeatherData()
    }

    /**
     * init UI
     */
    private fun initUI() {
        // TODO chan 위로 당겨 새로고침  ->  날씨 데이터 갱신 로직 필요 (항상 갱신이 아니라 내부 저장 데이터 날짜 확인하여)

        with(binding.srlContainer) {
            this.setOnRefreshListener {
                reqWeatherData()
                this.isRefreshing = false
            }
        }
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
                viewModel.saveLocalWeatherData(it, MainViewModel.WeatherType.NOW)

            }

            weatherRightNowJson.observe((activity as BaseActivity<*>)) {
                L.d("weatherRightNowJson observe : $it")
                "Ultra Success".toast((activity as BaseActivity<*>))

                val t1hList = arrayListOf<JsonElement>()

                var tpm = ""
                it.forEach {
                    if (it.asJsonObject.asString("category") == "T1H") {
                        t1hList.add(it.asJsonObject)
                        tpm += "time : ${it.asJsonObject.asString("fcstTime")} value : ${it.asJsonObject.asString("fcstValue")} \n"
                    }
                }
                val t = "${t1hList[0].asJsonObject.asString("fcstTime")} \n ${t1hList[0].asJsonObject.asString("fcstValue")}"

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

            weatherMidTaJson.observe((activity as BaseActivity<*>)) {
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

            localWeatherNowDataList.observe((activity as BaseActivity<*>)) {
                L.d("localWeatherData observe : $it")
                this@WeatherFragment.localWeatherNowDataList.clear()
                this@WeatherFragment.localWeatherNowDataList.addAll(it)
                deleteBeforeLocalWeatherData(this@WeatherFragment.localWeatherNowDataList)

                // 시간별 날씨 정보
                val timeLineList: MutableList<WeatherTimeLineData> = mutableListOf()
                for (i in 0 until 20) {
                    var skyDrawable: Drawable? = null
                    if (this@WeatherFragment.localWeatherNowDataList.size > 0) {
                        with(this@WeatherFragment.localWeatherNowDataList[i]) {
                            // TODO chan 날씨 아이콘 재정렬 필요
                            skyDrawable = when(this.sky) {
                                "1" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_sunny) }
                                "2", "3" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_cloudy_a_lot) }
                                "4" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_cloudy) }
                                else -> { null }
                            }

                            skyDrawable = when(this.pty) {
                                "1" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_rainny) }
                                "5" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_rainny) }
                                else -> skyDrawable
                            }

                            timeLineList.add(
                                WeatherTimeLineData(
                                    date = this.time.toString().substring(0,8),
                                    time = this.time.toString().substring(8),
                                    drawable = skyDrawable,
                                    temperature = this.tmp,
                                    pop = this.pop
                                )
                            )
                        }
                    }

                }
                weatherTimeLineAdapter.value.changeItemList(timeLineList)
            }

            weatherMidTaJson.observe((activity as BaseActivity<*>)) {
                L.d("weatherMidTaJson observe : $it")
            }

            weatherMidFcstJson.observe((activity as BaseActivity<*>)) {
                L.d("weatherMidFcstJson observe : $it")
            }
        }

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
        reqWeatherMid()
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
     * 기상청 중기 예보 조회
     */
    private fun reqWeatherMid() {
        viewModel.getWeatherMid(
            mapOf(
                "serviceKey" to C.WeatherApi.API_KEY,
                "dataType" to "JSON",
                "pageNo" to "1",
                "numOfRows" to "10",
                "regId" to "11B10101",
                "tmFc" to "202211080600"
            )
        )

    }

}