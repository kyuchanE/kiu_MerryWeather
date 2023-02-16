package kiu.dev.merryweather.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.databinding.ActivityMainBinding
import kiu.dev.merryweather.ui.adapter.MainPageAdapter
import kiu.dev.merryweather.ui.fragment.MainFragment
import kiu.dev.merryweather.ui.fragment.SettingFragment
import kiu.dev.merryweather.ui.fragment.WeatherFragment
import kiu.dev.merryweather.viewmodel.MainViewModel
import kiu.dev.merryweather.utils.L
import kiu.dev.merryweather.utils.WidgetUpdateWorker
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId: Int = R.layout.activity_main
    private val viewModel: MainViewModel by viewModels()

    private lateinit var pageAdapter: MainPageAdapter

    private val workManager = WorkManager.getInstance(this)
    lateinit var widgetUpdateWorkInfo: LiveData<List<WorkInfo>>

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
        widgetUpdateWorkInfo = workManager.getWorkInfosByTagLiveData(C.WorkTag.WIDGET_UPDATE)
        initObserve()

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

    override fun onResume() {
        super.onResume()
        initWork()
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

    /**
     * init Work
     */
    private fun initWork() {
        // cancel work
        workManager.cancelAllWorkByTag(C.WorkTag.WIDGET_UPDATE)

        // work constraint
        val workConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)     // 배터리 부족상태가 아닐 때만 작동
            .build()
        // work request
        val workRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(2, TimeUnit.HOURS)
            .addTag(C.WorkTag.WIDGET_UPDATE)
            .setConstraints(workConstraints)
            .build()
        // work enqueue
        workManager.enqueue(workRequest)
    }

    /**
     * init Observe
     */
    private fun initObserve() {
        widgetUpdateWorkInfo.observe(this) {
            L.d("widgetUpdateWorkInfo observe : $it")
        }
    }

}