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
import dev.kyu.main.R
import dev.kyu.main.databinding.ActivityMainBinding
import dev.kyu.ui.base.BaseActivity
import dev.kyu.ui.utils.L
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId: Int = R.layout.activity_main

    private val workManager = WorkManager.getInstance(this)
    lateinit var widgetUpdateWorkInfo: LiveData<List<WorkInfo>>

    private var weatherTimeLineAdapter = WeatherTimeLineAdapter(this,mutableListOf())

    private val viewModel by viewModels<MainViewModel> ()

    override fun init() {
        // TODO chan MainActivity 로직 정리 필요 -> WeatherFragment
        // TODO chan Main에서 필요한 로직은 무엇?

        //        widgetUpdateWorkInfo = workManager.getWorkInfosByTagLiveData(C.WorkTag.WIDGET_UPDATE)
        observeViewModel()

        binding.rvToday.adapter = weatherTimeLineAdapter

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
            }
        }

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