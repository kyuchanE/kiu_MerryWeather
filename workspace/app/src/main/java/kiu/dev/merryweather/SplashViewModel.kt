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
import dev.kyu.ui.utils.dateToString
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.joinAll
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

    fun reqWeatherData(
        pageNo: Int,
        nx: Int,
        ny: Int,
    ) {
        viewModelScope.launch {
            _loadingController.emit(true)

            midLandFcstJob(
                100, pageNo,
            ).join()

            midTaJob(
                100, pageNo,
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

    private fun midLandFcstJob(
        numOfRows: Int,
        pageNo: Int,
    ): Job = viewModelScope.launch {
        midWeatherUseCase.getMidLandFcsData(
            numOfRows,
            pageNo,
            "11B00000",
            getMidBaseTime()
        ).catch {
        }.collectLatest { midLandFcstData ->
            midLandFcstData?.let { midLandFcstData ->
                midLandFcstData?.let { data ->
                    data.midLandFcstItems?.let { landFcstItem ->
                        val resultWeatherDataList = mutableListOf<WeatherData>()

                        resultWeatherDataList.add(
                            WeatherData().apply {
                                dateTime = getMidBaseTime(2)
                                pop = if (landFcstItem.rnSt3Am < landFcstItem.rnSt3Pm) landFcstItem.rnSt3Pm.toString() else landFcstItem.rnSt3Am.toString()
                                sky = getMidLandFcstSkyStr(landFcstItem.wf3Am, landFcstItem.wf3Pm)
                                pty = getMidLandFcstPtyStr(landFcstItem.wf3Am, landFcstItem.wf3Pm)
                            }
                        )

                        resultWeatherDataList.add(
                            WeatherData().apply {
                                dateTime = getMidBaseTime(3)
                                pop = if (landFcstItem.rnSt4Am < landFcstItem.rnSt4Pm) landFcstItem.rnSt4Pm.toString() else landFcstItem.rnSt4Am.toString()
                                sky = getMidLandFcstSkyStr(landFcstItem.wf4Am, landFcstItem.wf4Pm)
                                pty = getMidLandFcstPtyStr(landFcstItem.wf4Am, landFcstItem.wf4Pm)
                            }
                        )

                        resultWeatherDataList.add(
                            WeatherData().apply {
                                dateTime = getMidBaseTime(4)
                                pop = if (landFcstItem.rnSt5Am < landFcstItem.rnSt5Pm) landFcstItem.rnSt5Pm.toString() else landFcstItem.rnSt5Am.toString()
                                sky = getMidLandFcstSkyStr(landFcstItem.wf5Am, landFcstItem.wf5Pm)
                                pty = getMidLandFcstPtyStr(landFcstItem.wf5Am, landFcstItem.wf5Pm)
                            }
                        )

                        resultWeatherDataList.add(
                            WeatherData().apply {
                                dateTime = getMidBaseTime(5)
                                pop = if (landFcstItem.rnSt6Am < landFcstItem.rnSt6Pm) landFcstItem.rnSt6Pm.toString() else landFcstItem.rnSt6Am.toString()
                                sky = getMidLandFcstSkyStr(landFcstItem.wf6Am, landFcstItem.wf6Pm)
                                pty = getMidLandFcstPtyStr(landFcstItem.wf6Am, landFcstItem.wf6Pm)
                            }
                        )

                        resultWeatherDataList.add(
                            WeatherData().apply {
                                dateTime = getMidBaseTime(6)
                                pop = if (landFcstItem.rnSt7Am < landFcstItem.rnSt7Pm) landFcstItem.rnSt7Pm.toString() else landFcstItem.rnSt7Am.toString()
                                sky = getMidLandFcstSkyStr(landFcstItem.wf7Am, landFcstItem.wf7Pm)
                                pty = getMidLandFcstPtyStr(landFcstItem.wf7Am, landFcstItem.wf7Pm)
                            }
                        )

                        saveWeatherData(resultWeatherDataList, MID_LAND_FCST_TYPE)
                    }
                }
            }
        }
    }

    private fun midTaJob(
        numOfRows: Int,
        pageNo: Int
    ): Job = viewModelScope.launch {
        midWeatherUseCase.getMidTaData(
            numOfRows,
            pageNo,
            "11B10101",
            getMidBaseTime()
        ).catch {

        }.collectLatest { midTaResponse ->
            midTaResponse?.let { data ->
                data.midTaItems?.let { taItem ->
                    val resultWeatherDataList = mutableListOf<WeatherData>()

                    resultWeatherDataList.add(
                        WeatherData().apply {
                            dateTime = getMidBaseTime(2)
                            tmx = taItem.taMax3.toString()
                            tmn = taItem.taMin3.toString()
                        }
                    )

                    resultWeatherDataList.add(
                        WeatherData().apply {
                            dateTime = getMidBaseTime(3)
                            tmx = taItem.taMax4.toString()
                            tmn = taItem.taMin4.toString()
                        }
                    )

                    resultWeatherDataList.add(
                        WeatherData().apply {
                            dateTime = getMidBaseTime(4)
                            tmx = taItem.taMax5.toString()
                            tmn = taItem.taMin5.toString()
                        }
                    )

                    resultWeatherDataList.add(
                        WeatherData().apply {
                            dateTime = getMidBaseTime(5)
                            tmx = taItem.taMax6.toString()
                            tmn = taItem.taMin6.toString()
                        }
                    )

                    resultWeatherDataList.add(
                        WeatherData().apply {
                            dateTime = getMidBaseTime(6)
                            tmx = taItem.taMax7.toString()
                            tmn = taItem.taMin7.toString()
                        }
                    )

                    saveWeatherData(resultWeatherDataList, MID_TA_TYPE)
                }
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
                                        it.pop = it.pop
                                        it.reh = it.reh
                                        it.tmn = it.tmn
                                        it.tmx = it.tmx
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
                                        it.tmn = weatherData.tmn
                                        it.tmx = weatherData.tmx
                                    }
                                }
                                MID_TA_TYPE -> {
                                    findLatest<WeatherData>(this@with)?.let {
                                        it.tmn = weatherData.tmn
                                        it.tmx = weatherData.tmx
                                        it.sky = it.sky
                                        it.pty = it.pty
                                        it.pop = it.pop
                                    }
                                }
                                MID_LAND_FCST_TYPE -> {
                                    findLatest<WeatherData>(this@with)?.let {
                                        it.tmn = it.tmn
                                        it.tmx = it.tmx
                                        it.sky = weatherData.sky
                                        it.pty = weatherData.pty
                                        it.pop = weatherData.pop
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