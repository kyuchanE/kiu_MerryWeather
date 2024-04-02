package kiu.dev.merryweather

import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dev.kyu.main.ui.MainActivity
import dev.kyu.ui.base.BaseActivity
import dev.kyu.ui.utils.setStatusBarTransparent
import kiu.dev.merryweather.databinding.ActivitySplashBinding

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override val layoutId: Int = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO chan : Splash Screen UI
        installSplashScreen()
        super.onCreate(savedInstanceState)

        initView()
        observeViewModel()

    }

    override fun initView() {
        setStatusBarTransparent()
        defaultPadding(binding.clContainer)
    }

    private fun moveMain() {
        startActivity(
            Intent(
                this@SplashActivity,
                MainActivity::class.java
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        )
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    /**
     * 날씨 데이터 요청
     */
    private fun reqWeatherData() {

    }


    /**
     * viewModel observe 세팅
     */
    private fun observeViewModel() {

    }


}