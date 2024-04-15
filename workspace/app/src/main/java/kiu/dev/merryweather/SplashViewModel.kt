package kiu.dev.merryweather

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kyu.domain.model.MidLandFcstData
import dev.kyu.domain.model.VilageFcstData
import dev.kyu.domain.model.WeatherData
import dev.kyu.domain.usecase.GetMidWeatherUseCase
import dev.kyu.domain.usecase.GetVilageWeatherUseCase
import dev.kyu.ui.base.BaseViewModel
import dev.kyu.ui.utils.L
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val midWeatherUseCase: GetMidWeatherUseCase,
    private val vilageWeatherUseCase: GetVilageWeatherUseCase,
): BaseViewModel() {

    private val _loadingController = MutableSharedFlow<Boolean>()
    var loadingController = _loadingController.asSharedFlow()

    private val _midWeatherFcstData = MutableSharedFlow<MidLandFcstData>()
    var midWeatherFcstData = _midWeatherFcstData.asSharedFlow()

    private val _ultraWeatherResponse = MutableSharedFlow<WeatherData>()
    var ultraWeatherResponse = _ultraWeatherResponse.asSharedFlow()

    fun getMidWeatherFcst(
        numOfRows: Int,
        pageNo: Int,
        regId: String,
        tmFc: String
    ) {
        viewModelScope.launch {
            midWeatherUseCase(
                numOfRows, pageNo, regId, tmFc
            ).onStart {
                _loadingController.emit(true)
            }.catch {
                _loadingController.emit(false)
            }.collectLatest { midLandFcstData ->
                _loadingController.emit(false)
                midLandFcstData?.let {
                    _midWeatherFcstData.emit(it)
                }
            }
        }
    }

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
            }.collectLatest { it ->
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

                        saveWeatherData(resultWeatherDataList, ULTRA_WEATHER_TYPE)

                    }
                }

            }
        }
    }

    fun getVilageFcst(
        numOfRows: Int,
        pageNo: Int,
        nx: Int,
        ny: Int
    ) {
        viewModelScope.launch {
            vilageFcstJob(
                numOfRows,
                pageNo,
                nx,
                ny
            ).join()
        }
    }

    fun reqWeatherData(
        pageNo: Int,
        regId: String,
        tmFc: String,
        nx: Int,
        ny: Int,
    ) {
        viewModelScope.launch {
            _loadingController.emit(true)

            midWeatherFcstJob(
                100, pageNo, regId, tmFc
            ).join()

            vilageFcstJob(
                700, pageNo, nx, ny
            ).join()

            ultraStrFcstJob(
                100, pageNo, nx, ny
            ).join()

            _loadingController.emit(false)
        }
    }

    private fun midWeatherFcstJob(
        numOfRows: Int,
        pageNo: Int,
        regId: String,
        tmFc: String
    ): Job = viewModelScope.launch {
        midWeatherUseCase(
            numOfRows, pageNo, regId, tmFc
        ).catch {
        }.collectLatest { midLandFcstData ->
            midLandFcstData?.let {
                _midWeatherFcstData.emit(it)
            }
        }
    }

    private fun ultraStrFcstJob(
        numOfRows: Int,
        pageNo: Int,
        nx: Int,
        ny: Int
    ): Job = viewModelScope.launch {
        vilageWeatherUseCase.getUltraStrFcstData(
            numOfRows,
            pageNo,
            nx,
            ny,
            getBaseDate(),
            getUltraBaseTime(),
        ).catch {

        }.collectLatest { vilageFcstResponse ->
            vilageFcstResponse?.let { data ->
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

                    saveWeatherData(resultWeatherDataList, ULTRA_WEATHER_TYPE)

                }
            }

        }
    }

    private fun vilageFcstJob(
        numOfRows: Int,
        pageNo: Int,
        nx: Int,
        ny: Int
    ): Job = viewModelScope.launch {
        vilageWeatherUseCase.getVilageFcstData(
            numOfRows,
            pageNo,
            nx,
            ny,
            getBaseDate(),
            getVilageBaseTime(),
        ).catch {

        }.collectLatest { vilageFcstResponse ->
            vilageFcstResponse?.let { data ->
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
                            VilageFcstData.CATEGORY_TEMP_HOUR -> {
                                resultWeatherDataList[position].t1h = item.fcstValue
                            }
                            VilageFcstData.CATEGORY_SKY -> {
                                resultWeatherDataList[position].sky = item.fcstValue
                            }
                            VilageFcstData.CATEGORY_PRECIPITATION_TYPE -> {
                                resultWeatherDataList[position].pty = item.fcstValue
                            }
                            VilageFcstData.CATEGORY_RAIN_HOUR -> {
                                resultWeatherDataList[position].rn1 = item.fcstValue
                            }
                            VilageFcstData.CATEGORY_REH -> {
                                resultWeatherDataList[position].reh = item.fcstValue
                            }
                            VilageFcstData.CATEGORY_POP -> {
                                resultWeatherDataList[position].pop = item.fcstValue
                            }
                            VilageFcstData.CATEGORY_TMN -> {
                                resultWeatherDataList[position].tmn = item.fcstValue
                            }
                            VilageFcstData.CATEGORY_TMX -> {
                                resultWeatherDataList[position].tmx = item.fcstValue
                            }
                        }
                    }

                    saveWeatherData(resultWeatherDataList, VILAGE_WEATHER_TYPE)

                }
            }
        }
    }

    private fun saveWeatherData(
        weatherDataList: MutableList<WeatherData>,
        weatherResponseType: String,
    ) {
        viewModelScope.launch {
            weatherDataList.forEach { weatherData ->
                with(vilageWeatherUseCase.getRealm().query<WeatherData>("dateTime = $0", weatherData.dateTime).first().find()) {
                    if (this != null) {
                        vilageWeatherUseCase.getRealm().writeBlocking {
                            when(weatherResponseType) {
                                ULTRA_WEATHER_TYPE -> {
                                    findLatest<WeatherData>(this@with)?.let {
                                        it.t1h = weatherData.t1h
                                        it.reh = weatherData.reh
                                        it.rn1 = weatherData.rn1
                                        it.sky = weatherData.sky
                                        it.pty = weatherData.pty
                                    }
                                }
                                VILAGE_WEATHER_TYPE -> {
                                    findLatest<WeatherData>(this@with)?.let {
                                        it.t1h = weatherData.t1h
                                        it.rn1 = weatherData.rn1
                                        it.sky = weatherData.sky
                                        it.pty = weatherData.pty
                                        it.pop = weatherData.pop
                                        it.reh = weatherData.reh
                                        if (it.tmn > weatherData.tmn) {
                                            it.tmn = weatherData.tmn
                                        }
                                        if (it.tmx < weatherData.tmx) {
                                            it.tmx = weatherData.tmx
                                        }
                                    }
                                }
                                else -> {}
                            }
                        }
                    } else {
                        vilageWeatherUseCase.saveWeatherData(weatherData)
                    }
                }

            }

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


}