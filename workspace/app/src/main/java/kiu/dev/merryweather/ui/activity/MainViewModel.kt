package kiu.dev.merryweather.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.base.BaseViewModel
import kiu.dev.merryweather.repository.WeatherRepository
import kiu.dev.merryweather.utils.L
import kiu.dev.merryweather.utils.asJsonArray
import kiu.dev.merryweather.utils.asJsonObject
import kiu.dev.merryweather.utils.asString

class MainViewModel (
    private val weatherRepository: WeatherRepository
) : BaseViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> get() = _isLoading

    private val _showError = MutableLiveData<String>()
    val showError : LiveData<String> get() = _showError

    private val _weatherNowJson = MutableLiveData<List<JsonElement>>()
    val weatherNowJson : LiveData<List<JsonElement>> get() = _weatherNowJson

    private val _weatherUltraNowJson = MutableLiveData<List<JsonElement>>()
    val weatherUltraNowJson : LiveData<List<JsonElement>> get() = _weatherUltraNowJson

    private val _weatherWeekJson = MutableLiveData<JsonObject>()
    val weatherWeekJson : LiveData<JsonObject> get() = _weatherWeekJson

    /**
     * 기상청 단기 예보 정보 (1일 8회)
     * @param ServiceKey  API KEY
     * @param dataType  JSON, XML
     * @param pageNo : 페이지 번호
     * @param numOfRows  한 페이지 결과 수
     * @param base_date  발표일자
     * @param base_time  발표시각 (0210, 0510, 0810, 1110, 1410, 1710, 2010, 2310)
     * @param nx  예보지점 X 좌표
     * @param ny  예보지점 Y 좌표
     */
    fun getNow(
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
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { e ->
                    L.d("e : $e")
                }
                .doOnNext { json ->
                    L.d("json : $json")
                    if (isWeatherSuccess(json)) {
                        val itemsJsonArray: List<JsonElement> =
                            json.asJsonObject("response")
                                .asJsonObject("body")
                                .asJsonObject("items")
                                .asJsonArray("item")
                                .filter {
                                    it.asJsonObject.asString("category") == "POP" ||
                                            it.asJsonObject.asString("category") == "PCP" ||
                                            it.asJsonObject.asString("category") == "TMP" ||
                                            it.asJsonObject.asString("category") == "SKY" ||
                                            it.asJsonObject.asString("category") == "TMN" ||
                                            it.asJsonObject.asString("category") == "TMX" ||
                                            it.asJsonObject.asString("category") == "PTY"
                                }
                        _weatherNowJson.postValue(itemsJsonArray)
                    }
                }
                .doFinally{
                    _isLoading.postValue(false)
                }
                .subscribe()
        )

    }

    /**
     * 기상청 초단기 예보 정보
     * @param ServiceKey  API KEY
     * @param dataType  JSON, XML
     * @param pageNo  페이지 번호
     * @param numOfRows  한 페이지 결과 수
     * @param base_date  발표일자
     * @param base_time  발표시각
     * @param nx  예보지점 X 좌표
     * @param ny  예보지점 Y 좌표
     */
    fun getUltraNow(
        params: Map<String, Any?> = mapOf()
    ) {
        // TODO 새로고침 또는 앱 첫 진입시

        _isLoading.postValue(true)
        addDisposable(
            weatherRepository.getUltraNow(
                params = params
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { e ->
                    L.d("e : $e")
                }
                .doOnNext { json ->
                    L.d("json : $json")

                    if (isWeatherSuccess(json)){
                        val itemsJsonArray: List<JsonElement> =
                            json.asJsonObject("response")
                                .asJsonObject("body")
                                .asJsonObject("items")
                                .asJsonArray("item")
                                .filter {
                                    it.asJsonObject.asString("category") == "T1H" ||
                                            it.asJsonObject.asString("category") == "RN1" ||
                                            it.asJsonObject.asString("category") == "SKY" ||
                                            it.asJsonObject.asString("category") == "PTY"
                            }

                        L.d("itemsJsonArray $itemsJsonArray")
                        _weatherUltraNowJson.postValue(itemsJsonArray)
                    }
                }
                .doFinally{
                    _isLoading.postValue(false)
                }
                .subscribe()
        )

    }

    /**
     * 기상청 중기 기온 정보
     * @param ServiceKey  API KEY
     * @param dataType  JSON, XML
     * @param pageNo  페이지 번호
     * @param numOfRows  한 페이지 결과 수
     * @param regId  예보구역 코드 (11B10101 서울)
     * @param tmFc  발표시각 (일 2회 06:00 18:00 생성 YYYYMMDD0600(1800))
     */
    fun getWeek(
        params: Map<String, Any?> = mapOf()
    ) {
        _isLoading.postValue(true)
        addDisposable(
            weatherRepository.getWeek(
                params = params
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { e ->
                    L.d("e : $e")
                }
                .doOnNext { json ->
                    L.d("json : $json")
                    if (isWeatherSuccess(json)){
                        _weatherWeekJson.postValue(json)
                    }
                }
                .doFinally{
                    _isLoading.postValue(false)
                }
                .subscribe()
        )

    }

    /**
     * 기상청 Api ResultCode
     * @param data  json data
     * @return 00:정상, 01:어플리케이션 에러, 02:DB에러, 03:데이터 없음,
     * 04:HTTP에러, 05:서비스 연결 실패, 10:잘못된 요청 파라미터, 11:필수요청 에러,
     * 20:서비스 접근 거부, 21:사용할 수 없는 키, 22:서비스 요청제한 횟수 초과,
     * 30:등록되지 않은 키, 31:기한만료된 키, 32:등록되지 않은 IP, 33: 서명하지 않은 호출
     * 99:기타
     */
    private fun isWeatherSuccess(data: JsonObject): Boolean {
        val resultCode: String = try {
            data.asJsonObject("response")
                .asJsonObject("header")
                .asString("resultCode")
        } catch (e: Exception) {
            ""
        }

        if (resultCode == "00") {
            return true
        } else {
            _showError.postValue(
                data.asJsonObject("response")
                    .asJsonObject("header")
                    .asString("resultMsg")
            )
            return false
        }
    }

}
