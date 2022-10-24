package kiu.dev.merryweather.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseFragment
import kiu.dev.merryweather.databinding.FragmentMainBinding

class MainFragment: BaseFragment<FragmentMainBinding>() {
    override val layoutId: Int = R.layout.fragment_main

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}