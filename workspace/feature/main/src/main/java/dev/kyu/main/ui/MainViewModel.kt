package dev.kyu.main.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kyu.domain.model.VilageFcstData
import dev.kyu.domain.model.WeatherData
import dev.kyu.domain.usecase.GetVilageWeatherUseCase
import dev.kyu.ui.base.BaseViewModel
import dev.kyu.ui.utils.L
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val vilageWeatherUseCase: GetVilageWeatherUseCase,
): BaseViewModel() {

    private val _loadingController = MutableSharedFlow<Boolean>()
    var loadingController = _loadingController.asSharedFlow()

    private val _ultraWeatherResponse = MutableSharedFlow<WeatherData>()
    var ultraWeatherResponse = _ultraWeatherResponse.asSharedFlow()

    private val _allWeatherData = MutableSharedFlow<List<WeatherData>>()
    var allWeatherData = _allWeatherData.asSharedFlow()

    fun getUltraWeatherFcst(
        numOfRows: Int,
        pageNo: Int,
        nx: Int,
        ny: Int
    ) {
        viewModelScope.launch {
            vilageWeatherUseCase.getUltraStrFcstData(
                numOfRows,
                pageNo,
                nx,
                ny,
                getBaseDate(),
                getUltraBaseTime(),
            ).onStart {
                _loadingController.emit(true)
            }.catch {
                _loadingController.emit(false)
            }.collectLatest {
                _loadingController.emit(false)

                it?.let { data ->
                    data.vilageFcstItems?.let { itemList ->
                        val resultDateTimeStrList = mutableListOf<String>()
                        val resultWeatherDataList = mutableListOf<WeatherData>()
                        itemList.forEach { item ->
                            val dateTimeStr = item.fcstDate + item.fcstTime
                            if (!resultDateTimeStrList.contains(dateTimeStr)) {
                                resultDateTimeStrList.add(dateTimeStr)
                                resultWeatherDataList.add(WeatherData().apply {
                                    dateTime = dateTimeStr
                                })
                            }
                            val position: Int = resultDateTimeStrList.indexOf(dateTimeStr)

                            when (item.category) {
                                VilageFcstData.CATEGORY_ULTRA_TEMP_HOUR -> {
                                    resultWeatherDataList[position].t1h = item.fcstValue
                                }
                                VilageFcstData.CATEGORY_SKY -> {
                                    resultWeatherDataList[position].sky = item.fcstValue
                                }
                                VilageFcstData.CATEGORY_PRECIPITATION_TYPE -> {
                                    resultWeatherDataList[position].pty = item.fcstValue
                                }
                                VilageFcstData.CATEGORY_ULTRA_RAIN_HOUR -> {
                                    resultWeatherDataList[position].rn1 = item.fcstValue
                                }
                                VilageFcstData.CATEGORY_REH -> {
                                    resultWeatherDataList[position].reh = item.fcstValue
                                }
                            }
                        }

//                        saveWeatherData(
//                            WeatherData().apply {
//                                this.dateTime = "202404091435"
//                            }
//                        )
                    }
                }

            }
        }
    }


    private fun saveWeatherData(
        weatherData: WeatherData
    ) {
        viewModelScope.launch {
            vilageWeatherUseCase.saveWeatherData(weatherData)
            val list = vilageWeatherUseCase.getAllWeatherData()
            list.forEach {
                L.d("saveWeatherData getWeatherData : ${it.dateTime}")
            }
        }
    }

    private fun getNow(): String {
        val calendar = Calendar.getInstance()
        return calendar.time.dateToString("yyyyMMddHH") + "00"
    }

    fun getNowWeatherData() {

        viewModelScope.launch {
            with(vilageWeatherUseCase.getRealm().query<WeatherData>("dateTime = $0", getNow()).find()) {
                L.d("getNowWeatherData getNow : ${getNow()} ")
                this.forEach {
                    L.d("getNowWeatherData : ${it.dateTime} , ${it.rn1}")
                }
                if (this.size > 0)
                    _ultraWeatherResponse.emit(this[0])
            }
        }

    }

    fun getAllWeatherData() {
        viewModelScope.launch {
            _allWeatherData.emit(vilageWeatherUseCase.getAllWeatherData())
        }
    }


}