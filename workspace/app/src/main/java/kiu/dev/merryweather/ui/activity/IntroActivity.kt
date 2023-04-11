package kiu.dev.merryweather.ui.activity

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.gson.JsonElement
import dagger.hilt.android.AndroidEntryPoint
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.local.widget.WidgetId
import kiu.dev.merryweather.databinding.ActivityIntroBinding
import kiu.dev.merryweather.ui.widget.SmallAppWidgetProvider
import kiu.dev.merryweather.utils.*
import kiu.dev.merryweather.viewmodel.MainViewModel

@AndroidEntryPoint
class IntroActivity : BaseActivity<ActivityIntroBinding>() {

    override val layoutId: Int = R.layout.activity_intro
    private val viewModel: MainViewModel by viewModels()
    private val widgetIdList = mutableListOf<WidgetId>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash init
        installSplashScreen()
        super.onCreate(savedInstanceState)

        initUI()
        startAnim()
        initViewModel()
        reqWeatherData()

        // TODO chan 미세먼지 데이터 필요 + 기상특보 데이터 필요  + 주간 기상 요약, 일일 기상 요약
        // TODO chan MainActivity launchMode
    }

    private fun initUI() {
        setStatusBarTransparent()
        defaultPadding(binding.clContainer)

        // Splash Animation init
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            splashScreen.setOnExitAnimationListener { splashScreenView ->
//
//                val animScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 2f)
//                val animScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 2f)
//                val animAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
//
//                ObjectAnimator.ofPropertyValuesHolder(splashScreenView.iconView, animAlpha, animScaleX, animScaleY).run {
//                    interpolator = AnticipateInterpolator()
//                    duration = 250L
//                    doOnEnd { splashScreenView.remove() }
//                    start()
//                }
//            }
//        }
    }

    private fun startAnim() {

    }

    private fun moveMain() {
        startActivity(
            Intent(
                this@IntroActivity,
                MainActivity::class.java
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        )
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    /**
     * 날씨 데이터 요청
     */
    private fun reqWeatherData()
    {
        reqWeatherNowTodayAll(
            C.WeatherData.Location.Seoul["nx"] ?: "",
            C.WeatherData.Location.Seoul["ny"] ?: ""
        )
        reqWeatherMid()
        reqWeatherNow(
            C.WeatherData.Location.Seoul["nx"] ?: "",
            C.WeatherData.Location.Seoul["ny"] ?: ""
        )
    }

    private fun reqWeatherNowTodayAll(nx: String, ny: String) {
        var nowDate: String = "yyyyMMdd".getTimeNow()
        var baseTime = "0210"

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
            ), false
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

    /**
     * viewModel observe 세팅
     */
    private fun initViewModel() {

        with(viewModel) {

            isInitFinish.observe(this@IntroActivity) {
                L.d("isIniFinish : $it")
                if (it) moveMain()
            }

            showError.observe(this@IntroActivity) {
                L.d("showError : $it")
            }

            weatherNowJson.observe(this@IntroActivity) {
                viewModel.saveLocalWeatherData(it, MainViewModel.WeatherType.NOW)
            }

            weatherRightNowJson.observe(this@IntroActivity) {
                viewModel.saveLocalWeatherData(it, MainViewModel.WeatherType.RIGHT_NOW)
            }

            weatherMidTaJson.observe(this@IntroActivity) {
                L.d("weatherMidTaJson : $it")
            }

            weatherMidFcstJson.observe(this@IntroActivity) {
                L.d("weatherMidFcstJson : $it")
            }
        }

    }


}