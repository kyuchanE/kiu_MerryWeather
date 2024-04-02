package dev.kyu.weather.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kyu.ui.base.BaseFragment
import dev.kyu.weather.R
import dev.kyu.weather.databinding.FragmentWeatherBinding
import org.json.JSONObject

class WeatherFragment: BaseFragment<FragmentWeatherBinding>() {
    override val layoutId: Int = R.layout.fragment_weather

//    private val widgetIdList = mutableListOf<WidgetId>()

//    private val localWeatherNowDataList = mutableListOf<WeatherNow>()
//    private val weatherTimeLineAdapter = lazy { WeatherTimeLineAdapter(mutableListOf()) }
//    private val weatherWeekLineAdapter = lazy { WeatherWeekLineAdapter(mutableListOf()) }

    private val cityAirDataList = mutableListOf<JSONObject>()

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

        observeViewModel()
        initView()

    }

    override fun onResume() {
        super.onResume()
    }

    /**
     * init UI
     */
    private fun initView() {
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
//            adapter = weatherTimeLineAdapter.value
        }

        with(binding.rvWeekLine) {
            layoutManager = LinearLayoutManager(context).apply {
                this.orientation = LinearLayoutManager.VERTICAL
            }
//            adapter = weatherWeekLineAdapter.value
        }
    }

    /**
     * viewModel observe 세팅
     */
    private fun observeViewModel() {

    }

    /**
     * 날씨 데이터 요청
     */
    private fun reqWeatherData() {

    }

}