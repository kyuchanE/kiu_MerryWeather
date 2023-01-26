package kiu.dev.merryweather.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.databinding.ActivityTestBinding

class TestActivity : BaseActivity<ActivityTestBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnTest.setOnClickListener {
            finish()
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_test
}