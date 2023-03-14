package kiu.dev.merryweather.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.base.BaseFragment
import kiu.dev.merryweather.databinding.FragmentSettingBinding
import kiu.dev.merryweather.utils.L

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인스타 연결
        binding.llInsta.setOnClickListener {
            goInstagram()
        }
        // 스마트 스토어 연결
        binding.llStore.setOnClickListener {
            goOutLinkUrl(getString(R.string.mw_smart_store))
        }

    }

    fun goOutLinkUrl(url: String) {
        val uri: Uri = Uri.parse(url)
        val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
        activity?.startActivity(intent)
    }

    fun goInstagram() {
        val uri: Uri = Uri.parse("http://instagram.com/_u/merry.weather_")
        val intent: Intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.instagram.android")
        }

        if ((activity as BaseActivity<*>).isIntentAvailable(intent)) {
            activity?.startActivity(intent)
        } else {
            activity?.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/merry.weather_"))
            )
        }
    }
}