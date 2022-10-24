package kiu.dev.merryweather.ui.activity

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.google.gson.JsonElement
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.databinding.ActivityMainBinding
import kiu.dev.merryweather.ui.fragment.MainFragment
import kiu.dev.merryweather.ui.fragment.MainPageAdapter
import kiu.dev.merryweather.ui.fragment.SettingFragment
import kiu.dev.merryweather.ui.fragment.WeatherFragment
import kiu.dev.merryweather.ui.widget.SmallAppWidgetProvider
import kiu.dev.merryweather.utils.L
import kiu.dev.merryweather.utils.asString
import kiu.dev.merryweather.utils.getTimeNow
import kiu.dev.merryweather.utils.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId: Int = R.layout.activity_main
    private val viewModel: MainViewModel by viewModel()
//    private val viewModel by viewModels<MainViewModel>()

    private lateinit var pageAdapter: MainPageAdapter

    /** init Fragment **/
    private val fragmentList = mutableListOf(
        WeatherFragment() as Fragment,
        MainFragment() as Fragment,
        SettingFragment() as Fragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()
        initViewPager()

        binding.tv1.setOnClickListener {
            binding.vpMain.setCurrentItem(0, false)
        }

        binding.tv2.setOnClickListener {
            binding.vpMain.setCurrentItem(1, false)
        }

        binding.tv3.setOnClickListener {
            binding.vpMain.setCurrentItem(2, false)
        }

    }

    /**
     * viewPager UI 세팅
     */
    private fun initViewPager(){
        pageAdapter = MainPageAdapter(this, fragmentList)
        with(binding.vpMain) {
            adapter = pageAdapter
            isUserInputEnabled = false      // 스와이프 막기
        }
    }

    /**
     * viewModel observe 세팅
     */
    private fun initViewModel() {

        with(viewModel) {

            isLoading.observe(this@MainActivity) {
                L.d("isLoading observe : $it")
            }

            showError.observe(this@MainActivity) {
                L.d("showError observe : $it")
            }

            weatherNowJson.observe(this@MainActivity) {
                L.d("weatherNowJson observe : $it")
            }

            weatherUltraNowJson.observe(this@MainActivity) {
                L.d("weatherUltraNowJson observe : $it")
            }

            weatherWeekJson.observe(this@MainActivity) {
                L.d("weatherWeekJson observe : $it")
            }
        }

    }

}