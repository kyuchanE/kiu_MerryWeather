package kiu.dev.merryweather.ui.fragment

import android.appwidget.AppWidgetManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonElement
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.base.BaseFragment
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.local.WeatherTimeLineData
import kiu.dev.merryweather.data.local.WeatherWeekLineData
import kiu.dev.merryweather.data.local.weather.mid.WeatherMid
import kiu.dev.merryweather.data.local.weather.now.WeatherNow
import kiu.dev.merryweather.data.local.widget.WidgetId
import kiu.dev.merryweather.databinding.FragmentWeatherBinding
import kiu.dev.merryweather.ui.adapter.WeatherTimeLineAdapter
import kiu.dev.merryweather.ui.adapter.WeatherWeekLineAdapter
import kiu.dev.merryweather.viewmodel.MainViewModel
import kiu.dev.merryweather.ui.widget.SmallAppWidgetProvider
import kiu.dev.merryweather.utils.*

class WeatherFragment: BaseFragment<FragmentWeatherBinding>() {
    override val layoutId: Int = R.layout.fragment_weather

    private val viewModel by activityViewModels<MainViewModel>()
    private val widgetIdList = mutableListOf<WidgetId>()
    private val localWeatherNowDataList = mutableListOf<WeatherNow>()
    private val weatherTimeLineAdapter = lazy { WeatherTimeLineAdapter(mutableListOf()) }
    private val weatherWeekLineAdapter = lazy { WeatherWeekLineAdapter(mutableListOf()) }

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
//        reqWeatherNow(
//            C.WeatherData.Location.Seoul["nx"] ?: "",
//            C.WeatherData.Location.Seoul["ny"] ?: ""
//        )

    }

    override fun onResume() {
        super.onResume()
    }

    /**
     * init UI
     */
    private fun initUI() {
        // TODO chan 위로 당겨 새로고침  ->  날씨 데이터 갱신 로직 필요 (항상 갱신이 아니라 내부 저장 데이터 날짜 확인하여)
        // TODO chan 오전 오후 표기 필요
        with(binding.srlContainer) {
            setOnRefreshListener {
                reqWeatherData()
                isRefreshing = false
            }
        }

        with(binding.rvTimeLine) {
            layoutManager = LinearLayoutManager(context).apply {
                this.orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = weatherTimeLineAdapter.value
        }

        with(binding.rvWeekLine) {
            layoutManager = LinearLayoutManager(context).apply {
                this.orientation = LinearLayoutManager.VERTICAL
            }
            adapter = weatherWeekLineAdapter.value
        }
    }

    /**
     * viewModel observe 세팅
     */
    private fun initViewModel() {

        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) {
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
                viewModel.saveLocalWeatherData(it, MainViewModel.WeatherType.NOW)

            }

            weatherRightNowJson.observe(viewLifecycleOwner) {
                L.d("weatherRightNowJson observe : $it")

                var time = ""
                var tmp = ""
                var pty = ""
                var sky = ""

                kotlin.run {
                    it.forEach {
                        if (it.asJsonObject.asString("category") == "T1H") {
                            tmp = it.asJsonObject.asString("fcstValue")
                            time = it.asJsonObject.asString("fcstTime")
                            return@run
                        }
                    }
                }

                kotlin.run {
                    it.forEach {
                        if (it.asJsonObject.asString("category") == "SKY") {
                            sky = it.asJsonObject.asString("fcstValue")
                            return@run
                        }
                    }
                }

                kotlin.run {
                    it.forEach {
                        if (it.asJsonObject.asString("category") == "PTY") {
                            pty = it.asJsonObject.asString("fcstValue")
                            return@run
                        }
                    }
                }

                binding.tvValueT.text = time
                binding.tvValue.text ="기온 ${tmp}° , 하늘 $sky, 강수 $pty"

                // 위젯 데이터 갱신
                this@WeatherFragment.widgetIdList.forEach {
                    it.id?.let {
                        SmallAppWidgetProvider.updateAppWidget(
                            (activity as BaseActivity<*>).context,
                            AppWidgetManager.getInstance((activity as BaseActivity<*>).context),
                            it,
                            tmp,
                            sky,
                            pty
                        )
                    }
                }

                viewModel.saveLocalWeatherData(it, MainViewModel.WeatherType.RIGHT_NOW)

            }

            widgetIdList.observe(viewLifecycleOwner) {
                L.d("widgetIdList observe : $it")
                this@WeatherFragment.widgetIdList.clear()
                it.forEach { widgetId ->
                    this@WeatherFragment.widgetIdList.add(widgetId)
                }
                L.d("fragment widgetIdList : ${this@WeatherFragment.widgetIdList}")
            }

            localWeatherNowDataList.observe(viewLifecycleOwner) { localDataList ->
                L.d("localWeatherData observe : $localDataList")
                this@WeatherFragment.localWeatherNowDataList.clear()
                this@WeatherFragment.localWeatherNowDataList.addAll(localDataList)
                deleteBeforeLocalWeatherData(this@WeatherFragment.localWeatherNowDataList)

                // API 날씨 데이터 가져오기 전 로컬 데이터로 미리 보여주기
                val timeNow = "yyyyMMddHH".getTimeNow() + "00"
                var listIndex = 0

                kotlin.run {
                    this@WeatherFragment.localWeatherNowDataList.forEachIndexed { index, weatherNow ->
                        if (timeNow.toLong() == weatherNow.time) {
                            binding.tvValue.text = weatherNow.tmp
                            binding.tvValueT.text = weatherNow.time.toString()
                            listIndex = index
                            return@run
                        }
                    }
                }

                // 시간별 날씨 정보
                val timeLineList: MutableList<WeatherTimeLineData> = mutableListOf()

                if (this@WeatherFragment.localWeatherNowDataList.size > 0) {
                    var pickItemsCount = this@WeatherFragment.localWeatherNowDataList.size - listIndex
                    var cntDataList: Int = pickItemsCount
                    if (pickItemsCount > 20) cntDataList = 20 + listIndex
                    for (i in listIndex until cntDataList) {
                        var skyDrawable: Drawable? = null
                        with(this@WeatherFragment.localWeatherNowDataList[i]) {
                            L.d("timeLine : ${this.time.toString()}")

                            // TODO chan 날씨 아이콘 로직 수정 필요
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

            localMidWeatherDataList.observe(viewLifecycleOwner) { localMidDataList ->
                L.d("localWeatherData observe : $localMidDataList")
                // 주간 날씨 정보
                val weekLineList: MutableList<WeatherWeekLineData> = mutableListOf()
                val weekDateList = "yyyyMMdd".getDateList()

                var isContainToday = false
                var todayIndex = 0
                localMidDataList.forEachIndexed { index, weatherMidData ->
                    if (weekDateList[0] == weatherMidData.date.toString()){
                        isContainToday = true
                        todayIndex = index
                    }
                }

                if (!isContainToday){
                    // 오늘 주간 날씨 데이터가 없는 경우
                    var saveMidDataList = mutableListOf<WeatherMid>()
                    weekDateList.forEach { weekDate ->
                        var midData = WeatherMid(date = weekDate.toLong())
                        this@WeatherFragment.localWeatherNowDataList.forEach { weatherNow ->
                            if (weekDate == weatherNow.time.toString().substring(0, 8)) {
                                if (weatherNow.tmn != null) {
                                    if (weatherNow.tmn.isNotEmpty()) midData.tmn = weatherNow.tmn
                                }
                                if(weatherNow.tmx != null) {
                                    if (weatherNow.tmx.isNotEmpty()) midData.tmx = weatherNow.tmx
                                }

                                if (weatherNow.time.toString().substring(8) == "0900") {
                                    midData.amPop = weatherNow.pop
                                    midData.amPty = weatherNow.pty
                                    midData.amSky = weatherNow.sky
                                } else if(weatherNow.time.toString().substring(8) == "1800") {
                                    midData.pmPop = weatherNow.pop
                                    midData.pmPty = weatherNow.pty
                                    midData.pmSky = weatherNow.sky
                                }
                            }
                        }
                        saveMidDataList.add(midData)
                    }
                    viewModel.saveLocalMidWeatherData(saveMidDataList)
                } else {
                    // 노출 가능한 주간 날씨 데이터가 모두 있는 경우
                    for (i in todayIndex until localMidDataList.size - 1) {
                        with(localMidDataList[i]) {
                            var strDayOfWeek = if (i == todayIndex) "오늘" else this.date.toString().getDayOfWeek()
                            var amSky: Drawable? = null
                            var pmSky: Drawable? = null
                            var selectPop = ""

                            // TODO chan 날씨 아이콘 로직 수정 필요
                            amSky = when(this.amSky) {
                                "1" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_sunny) }
                                "2", "3" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_cloudy_a_lot) }
                                "4" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_cloudy) }
                                else -> { null }
                            }

                            amSky = when(this.amPty) {
                                "1" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_rainny) }
                                "5" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_rainny) }
                                else -> amSky
                            }

                            pmSky = when(this.pmSky) {
                                "1" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_sunny) }
                                "2", "3" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_cloudy_a_lot) }
                                "4" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_cloudy) }
                                else -> { null }
                            }

                            pmSky = when(this.pmPty) {
                                "1" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_rainny) }
                                "5" -> { this@WeatherFragment.baseActivity.getDrawable(R.drawable.icon_rainny) }
                                else -> pmSky
                            }

                            val amPop = this.amPop.ifEmpty { "0" }
                            val pmPop = this.pmPop.ifEmpty { "0" }

                            selectPop =  if (amPop.toLong() > pmPop.toLong()) this.amPop
                            else this.pmPop

                            weekLineList.add(
                                WeatherWeekLineData(
                                    dayOfWeek = strDayOfWeek,
                                    amDrawable = amSky,
                                    pmDrawable = pmSky,
                                    tmn = this.tmn,
                                    tmx = this.tmx,
                                    pop = selectPop
                                )
                            )
                        }
                    }
                    weatherWeekLineAdapter.value.changeItemList(weekLineList)

                }

            }

            weatherMidTaJson.observe(viewLifecycleOwner) {
                L.d("weatherMidTaJson observe : $it")
            }

            weatherMidFcstJson.observe(viewLifecycleOwner) {
                L.d("weatherMidFcstJson observe : $it")
            }
        }

    }

    /**
     * 날씨 데이터 요청
     */
    private fun reqWeatherData()
    {
        reqWeatherMid()
        reqWeatherNow(
            C.WeatherData.Location.Seoul["nx"] ?: "",
            C.WeatherData.Location.Seoul["ny"] ?: ""
        )
    }

    /**
     * 기상청 초단기 예보 조회
     */
    private fun reqWeatherUltraNow(nx: String, ny: String) {
        var nowDate: String = "yyyyMMdd".getTimeNow()
        val nowTimeHour: Int = "HH".getTimeNow().toInt()
        val nowTimeMinute: Int = "mm".getTimeNow().toInt()

        L.d("reqWeatherUltraNow : $nowDate , hour : $nowTimeHour , minute : $nowTimeMinute")

        val baseTime: String = if (nowTimeMinute >= 30){
            String.format("%02d", nowTimeHour) + String.format("%02d", nowTimeMinute)
        } else {
            if (nowTimeHour == 0) {
                nowDate = "yyyyMMdd".getYesterday()
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
        var nowDate: String = "yyyyMMdd".getTimeNow()
        val nowHour: String = "HH".getTimeNow()
        val nowTime: String = "HHmm".getTimeNow()
        var baseTime = ""

        kotlin.run {
            C.WeatherData.WEATHER_NOW_GET_DATA_TIME.forEachIndexed { index, item ->
                L.d("reqWeatherNow nowTime : ${nowTime.toInt()}  , item : ${item.toInt()}")
                if (nowHour == "00" || nowHour == "01") {
                    nowDate = "yyyyMMdd".getYesterday()
                    baseTime = "2310"
                    return@run
                } else if (nowTime.toInt() in 200..210){
                    nowDate = "yyyyMMdd".getYesterday()
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

        // TODO chan numOfRows 더 큰 값을 변경 필요
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
     *  발표시각 (일 2회 06:00 18:00 생성 YYYYMMDD0600(1800))
     */
    private fun reqWeatherMid() {
        var nowDate: String = "yyyyMMdd".getTimeNow()
        val nowTimeHour: Int = "HH".getTimeNow().toInt()

        if (nowTimeHour > 18){
            nowDate += "1800"
        } else if (nowTimeHour > 6){
            nowDate += "0600"
        } else {
            nowDate = "yyyyMMdd".getYesterday() + "1800"
        }
        L.d("reqWeatherMid nowDate : $nowDate")
        viewModel.getWeatherMid(
            mapOf(
                "serviceKey" to C.WeatherApi.API_KEY,
                "dataType" to "JSON",
                "pageNo" to "1",
                "numOfRows" to "10",
                "regId" to "11B10101",
                "tmFc" to nowDate
            ),
            nowDate
        )

    }

}