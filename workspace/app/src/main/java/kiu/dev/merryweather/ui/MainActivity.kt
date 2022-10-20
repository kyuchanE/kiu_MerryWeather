package kiu.dev.merryweather.ui

import android.os.Bundle
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.databinding.ActivityMainBinding
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
            val nowDate: String = "YYYYMMdd".getTimeNow()
            val nowTimeHour: Int = "HH".getTimeNow().toInt()
            val nowTimeMinute: Int = "mm".getTimeNow().toInt()
            L.d("Now date : $nowDate , hour : $nowTimeHour , minute : $nowTimeMinute")

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
            }

            weatherUltraNowJson.observe(this@MainActivity) {
                L.d("weatherUltraNowJson observe : $it")
                "Ultra Success".toast(this@MainActivity)

                var tpm: String = ""
                it.forEach {
                    if (it.asJsonObject.asString("category") == "T1H") {
                        tpm += "time : ${it.asJsonObject.asString("fcstTime")} value : ${it.asJsonObject.asString("fcstValue")} \n"
                    }
                }

                binding.tvValue.text = tpm

            }

            weatherWeekJson.observe(this@MainActivity) {
                L.d("weatherWeekJson observe : $it")
            }
        }

    }

}