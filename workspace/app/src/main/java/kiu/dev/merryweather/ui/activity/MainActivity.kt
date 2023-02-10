package kiu.dev.merryweather.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.databinding.ActivityMainBinding
import kiu.dev.merryweather.ui.adapter.MainPageAdapter
import kiu.dev.merryweather.ui.fragment.MainFragment
import kiu.dev.merryweather.ui.fragment.SettingFragment
import kiu.dev.merryweather.ui.fragment.WeatherFragment
import kiu.dev.merryweather.ui.viewmodel.MainViewModel
import kiu.dev.merryweather.utils.L

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId: Int = R.layout.activity_main
    private val viewModel: MainViewModel by viewModels()

    private lateinit var pageAdapter: MainPageAdapter

    /** init Fragment **/
    private val fragmentList = mutableListOf(
        WeatherFragment() as Fragment,
        MainFragment() as Fragment,
        SettingFragment() as Fragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO chan MainActivity 로직 정리 필요 -> WeatherFragment

        // TODO chan Main에서 필요한 로직은 무엇?

        initUI()

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
     * init UI
     */
    private fun initUI() {
        pageAdapter = MainPageAdapter(this, fragmentList)
        with(binding.vpMain) {
            adapter = pageAdapter
            isUserInputEnabled = false      // 스와이프 막기
        }
    }

}