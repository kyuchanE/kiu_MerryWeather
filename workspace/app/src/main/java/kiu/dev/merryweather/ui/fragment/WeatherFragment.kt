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


}