package dev.kyu.main.ui

import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import dev.kyu.domain.model.WeatherData
import dev.kyu.main.R
import dev.kyu.main.databinding.ActivityMainBinding
import dev.kyu.ui.base.BaseActivity
import dev.kyu.ui.utils.L
import dev.kyu.ui.utils.getNextHour
import dev.kyu.ui.utils.getSkyDrawable
import dev.kyu.ui.utils.getToday
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId: Int = R.layout.activity_main

    private val workManager = WorkManager.getInstance(this)
    lateinit var widgetUpdateWorkInfo: LiveData<List<WorkInfo>>

    private var weatherTimeLineAdapter = WeatherTimeLineAdapter(this,mutableListOf())
    private var weatherWeekLineAdapter = WeatherWeekLineAdapter(this, mutableListOf())

    private val viewModel by viewModels<MainViewModel> ()

    override fun init() {
        // TODO chan MainActivity 로직 정리 필요 -> WeatherFragment
        // TODO chan Main에서 필요한 로직은 무엇?

        //        widgetUpdateWorkInfo = workManager.getWorkInfosByTagLiveData(C.WorkTag.WIDGET_UPDATE)
        observeViewModel()

        binding.rvToday.adapter = weatherTimeLineAdapter
        binding.rvWeek.adapter = weatherWeekLineAdapter

//        viewModel.getNowWeatherData()
        viewModel.getAllWeatherData()
    }

    override fun onResume() {
        super.onResume()

    }

    /**
     * init Work
     */
    private fun initWork() {
//        // cancel work
//        workManager.cancelAllWorkByTag(C.WorkTag.WIDGET_UPDATE)
//
//        // work constraint
//        val workConstraints = Constraints.Builder()
//            .setRequiresBatteryNotLow(true)     // 배터리 부족상태가 아닐 때만 작동
//            .build()
//        // work request
//        val workRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(2, TimeUnit.HOURS)
//            .addTag(C.WorkTag.WIDGET_UPDATE)
//            .setConstraints(workConstraints)
//            .build()
//        // work enqueue
//        workManager.enqueue(workRequest)
    }

    /**
     * init Observe
     */
    private fun observeViewModel() {

        lifecycleScope.launch {
            viewModel.loadingController.collect{

            }
        }

        lifecycleScope.launch {
            viewModel.ultraWeatherResponse.collect {

            }
        }

        lifecycleScope.launch {
            viewModel.allWeatherData.collect {
                weatherTimeLineAdapter.changeItemList(it.toMutableList())
                refreshNowWeather(it)
                refreshWeekWeather(it)
            }
        }

    }

    /**
     * 메인 상단 현시간 날씨 UI
     */
    private fun refreshNowWeather(weatherList: List<WeatherData>) {
        var nowWeather: WeatherData? = null
        weatherList.forEach {
            if (it.dateTime == "yyyyMMddHH".getToday() + "00") {
                nowWeather = it
                return@forEach
            }
        }

        val nowWeatherData: WeatherData = if (nowWeather == null) {
            weatherList.forEach {
                if (it.dateTime == getNextHour()) {
                    it
                    return@forEach
                }
            }
            WeatherData()
        } else {
          nowWeather!!
        }

        // TODO chan Now Weather UI
        binding.tvNow.text = "${nowWeatherData.dateTime} 기온 : ${nowWeatherData.t1h} , " +
                "최소 기온 : ${nowWeatherData.tmn} , 최대 기온 : ${nowWeatherData.tmx}"

        binding.ivSky.setImageDrawable(this.getSkyDrawable(nowWeatherData.pty, nowWeatherData.sky))

    }

    /**
     * 주간 날씨 UI
     */
    private fun refreshWeekWeather(weatherList: List<WeatherData>) {
        val weekWeatherDataList = mutableListOf<WeatherWeekLineData>()
        val dateList = mutableListOf<String>()

        weatherList.forEach {
            val dateWeather = it.dateTime.substring(0, 8)
            if ("yyyyMMdd".getToday().toInt() <= dateWeather.toInt()) {
                if (dateList.contains(it.dateTime.substring(0, 8))) {
                    val index = dateList.indexOf(it.dateTime.substring(0, 8))
                    weekWeatherDataList[index] = WeatherWeekLineData(
                        weekWeatherDataList[index].date,
                        if (weekWeatherDataList[index].sky < it.sky) it.sky else weekWeatherDataList[index].sky,
                        if (weekWeatherDataList[index].tmn > it.tmn) it.tmn else weekWeatherDataList[index].tmn,
                        if (weekWeatherDataList[index].tmx < it.tmx) it.tmx else weekWeatherDataList[index].tmx,
                        if (weekWeatherDataList[index].pty < it.pty) it.pty else weekWeatherDataList[index].pty,
                        if (weekWeatherDataList[index].pop < it.pop) it.pop else weekWeatherDataList[index].pop,
                    )

                } else {
                    dateList.add(dateWeather)
                    weekWeatherDataList.add(
                        WeatherWeekLineData(
                            it.dateTime.substring(0, 8),
                            it.sky,
                            it.tmn,
                            it.tmx,
                            it.pty,
                            it.pop
                        )
                    )
                }
            }
        }

        weekWeatherDataList.forEach {
            L.d("WeekWeatherData : ${it.date}")
        }

        weatherWeekLineAdapter.changeItemList(weekWeatherDataList)

    }

    private fun initFirebaseDatabase() {
        L.d("initFirebaseDatabase ")
        var database :FirebaseDatabase = Firebase.database("https://aos-todorim-default-rtdb.firebaseio.com/")
        var myRef: DatabaseReference = database.getReference("version")

        myRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var value = snapshot.getValue<String>()
                L.d("addValueEventListener value : $value")
            }

            override fun onCancelled(error: DatabaseError) {
                L.d("onCancelled : $error")
            }

        })
    }

}