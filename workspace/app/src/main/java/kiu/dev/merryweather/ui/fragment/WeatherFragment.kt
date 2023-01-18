package kiu.dev.merryweather.ui.fragment

import android.os.Bundle
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseFragment
import kiu.dev.merryweather.databinding.FragmentWeatherBinding

class WeatherFragment: BaseFragment<FragmentWeatherBinding>() {
    override val layoutId: Int = R.layout.fragment_weather

//    private val viewModel by activityViewModels<MainViewModel>()

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


    /**
     * init UI
     */
    private fun initUI() {
        // TODO chan 위로 당겨 새로고침  ->  날씨 데이터 갱신 로직 필요 (항상 갱신이 아니라 내부 저장 데이터 날짜 확인하여)

    }

    /**
     * viewModel observe 세팅
     */
    private fun initViewModel() {
        // TODO chan 날씨 데이터 받아오면 위젯 ID를 찾아 위젯 데이터 갱신 필요

        // TODO chan isLoading 로딩 노출/비노출 관련 로직 필요 -> MainActivity에서 로딩 노출
    }

}