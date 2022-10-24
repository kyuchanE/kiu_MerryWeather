package kiu.dev.merryweather.ui.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kiu.dev.merryweather.base.BaseFragment

class MainPageAdapter(
    fragmentActivity: FragmentActivity,
    private val fragmentList: MutableList<Fragment>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]

}