package kiu.dev.merryweather

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kyu.domain.model.MidLandFcstData
import dev.kyu.domain.model.WeatherData
import dev.kyu.domain.repository.WeatherRepository
import dev.kyu.domain.usecase.GetMidWeatherUseCase
import dev.kyu.domain.usecase.GetVilageWeatherUseCase
import dev.kyu.ui.base.BaseViewModel
import dev.kyu.ui.utils.L
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val midWeatherUseCase: GetMidWeatherUseCase,
    private val vilageWeatherUserCase: GetVilageWeatherUseCase,
): BaseViewModel() {

    private val _loadingController = MutableSharedFlow<Boolean>()
    var loadingController = _loadingController.asSharedFlow()

    private val _midWeatherFcstData = MutableSharedFlow<MidLandFcstData>()
    var midWeatherFcstData = _midWeatherFcstData.asSharedFlow()

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
        ny: Int,
        baseDate: String,
        baseTime: String,
    ) {
        viewModelScope.launch {
            vilageWeatherUserCase.getUltraStrFcstData(
                numOfRows, pageNo, nx, ny, baseDate, baseTime
            ).onStart {
                _loadingController.emit(true)
            }.catch {
                _loadingController.emit(false)
            }.collectLatest {
                _loadingController.emit(false)

//                saveWeatherData(
//                    WeatherData().apply {
//                        this.dateTime = "202404091435"
//                    }
//                )

            }
        }
    }

    fun reqWeatherData(
        numOfRows: Int,
        pageNo: Int,
        regId: String,
        tmFc: String,
        nx: Int,
        ny: Int,
        baseDate: String,
        baseTime: String,
    ) {
        viewModelScope.launch {
            _loadingController.emit(true)
            joinAll(
                midWeatherFcstJob(
                    numOfRows, pageNo, regId, tmFc
                ),
                ultraStrFcstJob(
                    numOfRows, pageNo, nx, ny, baseDate, baseTime
                ),
                vilageFcstJob(
                    numOfRows, pageNo, nx, ny, baseDate, baseTime
                ),
            )
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
        ny: Int,
        baseDate: String,
        baseTime: String,
    ): Job = viewModelScope.launch {
        vilageWeatherUserCase.getUltraStrFcstData(
            numOfRows, pageNo, nx, ny, baseDate, baseTime
        ).catch {

        }.collectLatest {

        }
    }

    private fun vilageFcstJob(
        numOfRows: Int,
        pageNo: Int,
        nx: Int,
        ny: Int,
        baseDate: String,
        baseTime: String,
    ): Job = viewModelScope.launch {
        vilageWeatherUserCase.getVilageFcstData(
            numOfRows, pageNo, nx, ny, baseDate, baseTime
        ).catch {

        }.collectLatest {

        }
    }

    private fun saveWeatherData(
        weatherData: WeatherData
    ) {
        viewModelScope.launch {
            vilageWeatherUserCase.saveWeatherData(weatherData)
            val list = vilageWeatherUserCase.getAllWeatherData()
            list.forEach {
                L.d("saveWeatherData getWeatherData : ${it.dateTime}")
            }
        }
    }

}