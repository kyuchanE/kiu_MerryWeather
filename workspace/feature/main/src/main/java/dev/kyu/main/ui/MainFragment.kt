package dev.kyu.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.kyu.main.R
import dev.kyu.main.databinding.FragmentMainBinding
import dev.kyu.ui.base.BaseFragment
import dev.kyu.ui.utils.L
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment: BaseFragment<FragmentMainBinding>() {

    override val layoutId: Int = R.layout.fragment_main

    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        const val KEY = "mainFragment"
        fun newInstance(): MainFragment = MainFragment().apply {
            arguments = Bundle().apply {
                val args = Bundle()
//                args.putString(KEY, data)
                arguments = args
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        viewModel.getNowWeatherData()
    }

    /**
     * init Observe
     */
    private fun observeViewModel() {

        lifecycleScope.launch {
            viewModel.ultraWeatherResponse.collect {
                L.d("WeatherData >> ${it.dateTime} : ${it.t1h} , ${it.reh} ")
                binding.tvTest.text = it.dateTime + " : " + it.t1h + " , " + it.reh
            }
        }
    }


}