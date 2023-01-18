package kiu.dev.merryweather.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.databinding.ActivityMainBinding
import kiu.dev.merryweather.utils.L
import kiu.dev.merryweather.utils.getTimeNow
import kiu.dev.merryweather.utils.getYesterday
import kiu.dev.merryweather.utils.toast

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId: Int = R.layout.activity_main
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO chan MainActivity 로직 정리 필요 -> WeatherFragment

        // TODO chan Main에서 필요한 로직은 무엇?

        initUI()
        initViewModel()
        reqWeatherNow(
            C.WeatherData.Location.Seoul["nx"] ?: "",
            C.WeatherData.Location.Seoul["ny"] ?: ""
        )

    }

    /**
     * init UI
     */
    private fun initUI() {
        // TODO chan Fragment생성 및 ViewPager swipe 막기
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

        baseTime.toast(this)

        // TODO chan nuoOfRows 줄여야함 / nx, ny 좌표 로직 추가 필요 
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
        with(viewModel){

            this.isLoading.observe(this@MainActivity) {
                if (it){
                    showLoading()
                } else {
                    hideLoading()
                }
            }

            this.weatherNowJson.observe(this@MainActivity) {
                L.d("weatherNowJson data : $it")
            }
        }
    }

}