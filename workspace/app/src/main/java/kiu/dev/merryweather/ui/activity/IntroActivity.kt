package kiu.dev.merryweather.ui.activity

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.databinding.ActivityIntroBinding
import kiu.dev.merryweather.utils.setStatusBarTransparent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IntroActivity : BaseActivity<ActivityIntroBinding>() {
    override val layoutId: Int = R.layout.activity_intro

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash init
        installSplashScreen()
        super.onCreate(savedInstanceState)

        initUI()
        moveMain()
    }

    private fun initUI() {

        setStatusBarTransparent()
        defaultPadding(binding.clContainer)

        // Splash Animation init
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->

                val animScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 2f)
                val animScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 2f)
                val animAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)

                ObjectAnimator.ofPropertyValuesHolder(splashScreenView.iconView, animAlpha, animScaleX, animScaleY).run {
                    interpolator = AnticipateInterpolator()
                    duration = 250L
                    doOnEnd { splashScreenView.remove() }
                    start()
                }
            }
        }
    }

    private fun moveMain() {
        GlobalScope.launch {
            delay(1200L)
            startActivity(
                Intent(
                    this@IntroActivity,
                    MainActivity::class.java
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            )
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finishIntro()
        }
    }

    private fun finishIntro() {
        finish()
    }

}