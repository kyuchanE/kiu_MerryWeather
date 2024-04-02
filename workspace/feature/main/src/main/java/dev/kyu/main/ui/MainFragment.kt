package dev.kyu.main.ui

import android.os.Bundle
import dev.kyu.main.R
import dev.kyu.main.databinding.FragmentMainBinding
import dev.kyu.ui.base.BaseFragment

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