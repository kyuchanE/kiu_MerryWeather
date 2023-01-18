package kiu.dev.merryweather.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.base.BaseViewModel
import kiu.dev.merryweather.data.local.weather.now.WeatherNow
import kiu.dev.merryweather.data.repository.WeatherRepository
import kiu.dev.merryweather.utils.L
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
)  : BaseViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> get() = _isLoading

    private val _showError = MutableLiveData<String>()
    val showError : LiveData<String> get() = _showError

    private val _weatherNowJson = MutableLiveData<JsonArray>()
    val weatherNowJson : LiveData<JsonArray> get() = _weatherNowJson

    private val _weatherRightNowJson = MutableLiveData<List<JsonElement>>()
    val weatherRightNowJson : LiveData<List<JsonElement>> get() = _weatherRightNowJson

    private val _weatherMidTaJson = MutableLiveData<JsonElement>()
    val weatherMidTaJson : LiveData<JsonElement> get() = _weatherMidTaJson

    private val _weatherMidFcstJson = MutableLiveData<JsonElement>()
    val weatherMidFcstJson : LiveData<JsonElement> get() = _weatherMidFcstJson

    private val _localWeatherNowDataList = MutableLiveData<List<WeatherNow>>()
    val localWeatherNowDataList: LiveData<List<WeatherNow>> get() = _localWeatherNowDataList

    enum class WeatherType {
        NOW, // 단기 예보
        RIGHT_NOW, // 초단기 예보
        MID
    }

    // TODO chan 위젯 ID 관련 로직 필요

    // TODO chan 단기/초단기/중기 데이터

    /**
     * 기상청 단기 예보 정보 (1일 8회) + 끝나고 초단기 예보 조회
     * @param ServiceKey  API KEY
     * @param dataType  JSON, XML
     * @param pageNo : 페이지 번호
     * @param numOfRows  한 페이지 결과 수
     * @param base_date  발표일자
     * @param base_time  발표시각 (0210, 0510, 0810, 1110, 1410, 1710, 2010, 2310)
     * @param nx  예보지점 X 좌표
     * @param ny  예보지점 Y 좌표
     */
    fun getNowWeather(
        params: Map<String, Any?> = mapOf()
    ) {
        _isLoading.postValue(true)

        // TODO 강수확률(POP), 일 최저기온(TMN), 일 최고기온(TMX), 오늘 시간별 날씨 정보
        //  base_tim 발표 시각 계산 필요 및 이전 예보 저장 후 사용
        //  (이미 존재하는 값이면 API 조회 안함)
        //  일 최고/최저 온도 미리 저장하여 사용

        addDisposable(
            weatherRepository.getNow(
                params = params
            )
                .doOnError { e ->
                    L.d("getNowWeather doOnError : $e")
                }
                .doOnNext { data ->
                    L.d("getNowWeather doOnNext data : $data")
                    _weatherNowJson.postValue(data)

                }
                .doFinally {
                    _isLoading.postValue(false)
                }
                .subscribe()
        )

    }
}