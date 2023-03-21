package kiu.dev.merryweather.ui.fragment

import android.os.Bundle
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseFragment
import kiu.dev.merryweather.databinding.FragmentMainBinding

class MainFragment: BaseFragment<FragmentMainBinding>() {
    override val layoutId: Int = R.layout.fragment_main

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
}