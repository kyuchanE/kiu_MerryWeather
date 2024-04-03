package kiu.dev.merryweather

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kyu.domain.model.MidLandFcstData
import dev.kyu.domain.repository.WeatherRepository
import dev.kyu.domain.usecase.GetMidWeatherUseCase
import dev.kyu.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val midWeatherUseCase: GetMidWeatherUseCase,
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


}