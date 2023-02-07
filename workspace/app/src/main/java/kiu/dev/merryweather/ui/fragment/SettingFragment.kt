package kiu.dev.merryweather.ui.fragment

import android.os.Bundle
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseFragment
import kiu.dev.merryweather.databinding.FragmentSettingBinding

class SettingFragment: BaseFragment<FragmentSettingBinding>() {
    override val layoutId: Int = R.layout.fragment_setting

    companion object {
        const val KEY = "settingFragment"
        fun newInstace(): SettingFragment = SettingFragment().apply {
            arguments = Bundle().apply {
                val args = Bundle()
//                args.putString(KEY, data)
                arguments = args
            }
        }
    }
}