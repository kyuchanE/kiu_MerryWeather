package kiu.dev.merryweather.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kiu.dev.merryweather.R
import kiu.dev.merryweather.base.BaseActivity
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.databinding.ActivityMainBinding
import kiu.dev.merryweather.utils.L
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId: Int = R.layout.activity_main
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.tvTest.setOnClickListener {
            viewModel.getWeather(
                mapOf(
                    "ServiceKey" to C.ApiLayer.key,
                    "dataType" to "JSON",
                    "pageNo" to "1",
                    "numOfRows" to "14",
                    "base_date" to "20221018",
                    "base_time" to "1400",
                    "nx" to "60",
                    "ny" to "127"
                )
            )
        }

        viewModel.weatherJson.observe(this) {
            L.d("weatherJson observe : $it")
        }

    }

}