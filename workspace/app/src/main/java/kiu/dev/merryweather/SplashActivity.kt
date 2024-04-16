package kiu.dev.merryweather

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.kyu.main.ui.MainActivity
import dev.kyu.ui.base.BaseActivity
import dev.kyu.ui.utils.L
import dev.kyu.ui.utils.setStatusBarTransparent
import kiu.dev.merryweather.databinding.ActivitySplashBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override val layoutId: Int = R.layout.activity_splash

    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO chan : Splash Screen UI
        installSplashScreen()
        super.onCreate(savedInstanceState)

    }

    override fun init() {
        observeViewModel()

        setStatusBarTransparent()
        defaultPadding(binding.clContainer)

        reqWeatherData()
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

        viewModel.reqWeatherData(
            1,
            55,
            127,
        )

    }


    /**
     * observe viewModel
     */
    private fun observeViewModel() {

        lifecycleScope.launch {
            viewModel.loadingController.collect {
                if (!it) {
                    moveMain()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.midWeatherFcstData.collect {
                L.d("MidLandWeatherFcstData : $it")
            }
        }

    }


}