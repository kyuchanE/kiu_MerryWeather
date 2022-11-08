package kiu.dev.merryweather.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.base.BaseViewModel
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.local.weather.now.WeatherNow
import kiu.dev.merryweather.data.local.widget.WidgetId
import kiu.dev.merryweather.data.repository.WeatherRepository
import kiu.dev.merryweather.data.repository.WidgetIdRepository
import kiu.dev.merryweather.utils.*

class MainViewModel (
    private val weatherRepository: WeatherRepository,
    private val widgetIdRepository: WidgetIdRepository
) : BaseViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> get() = _isLoading

    private val _showError = MutableLiveData<String>()
    val showError : LiveData<String> get() = _showError

    private val _weatherNowJson = MutableLiveData<List<JsonElement>>()
    val weatherNowJson : LiveData<List<JsonElement>> get() = _weatherNowJson

    private val _weatherRightNowJson = MutableLiveData<List<JsonElement>>()
    val weatherRightNowJson : LiveData<List<JsonElement>> get() = _weatherRightNowJson

    private val _weatherMidTaJson = MutableLiveData<JsonElement>()
    val weatherMidTaJson : LiveData<JsonElement> get() = _weatherMidTaJson

    private val _weatherMidFcstJson = MutableLiveData<JsonElement>()
    val weatherMidFcstJson : LiveData<JsonElement> get() = _weatherMidFcstJson

    private val _widgetIdList = MutableLiveData<List<WidgetId>>()
    val widgetIdList : LiveData<List<WidgetId>> get() = _widgetIdList

    private val _localWeatherNowDataList = MutableLiveData<List<WeatherNow>>()
    val localWeatherNowDataList: LiveData<List<WeatherNow>> get() = _localWeatherNowDataList

    enum class WeatherType {
        NOW, // 단기 예보
        RIGHT_NOW, // 초단기 예보
        MID
    }

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
                                    it.asJsonObject.asString("category") == "POP" ||    // 강수 확률
                                            it.asJsonObject.asString("category") == "PCP" || // 1시간 강수량
                                            it.asJsonObject.asString("category") == "TMP" || // 1시간 기온
                                            it.asJsonObject.asString("category") == "SKY" || // 하늘
                                            it.asJsonObject.asString("category") == "TMN" || // 일 최저 기온
                                            it.asJsonObject.asString("category") == "TMX" || // 일 최고 기온
                                            it.asJsonObject.asString("category") == "PTY"   // 강수 형태
                                }
                        _weatherNowJson.postValue(itemsJsonArray)
                    }
                }
                .doFinally{
                    var nowDate: String = "YYYYMMdd".getTimeNow()
                    val nowTimeHour: Int = "HH".getTimeNow().toInt()
                    val nowTimeMinute: Int = "mm".getTimeNow().toInt()

                    L.d("reqWeatherRightNow : $nowDate , hour : $nowTimeHour , minute : $nowTimeMinute")

                    val baseTime: String = if (nowTimeMinute >= 30){
                        String.format("%02d", nowTimeHour) + String.format("%02d", nowTimeMinute)
                    } else {
                        if (nowTimeHour == 0) {
                            nowDate = "YYYYMMdd".getYesterday()
                            "2330"
                        } else {
                            String.format("%02d", nowTimeHour-1) + "55"
                        }
                    }

                    getRightNowWeather(
                        mapOf(
                            "ServiceKey" to C.WeatherApi.API_KEY,
                            "dataType" to "JSON",
                            "pageNo" to "1",
                            "numOfRows" to "50",
                            "base_date" to nowDate,
                            "base_time" to baseTime,
                            "nx" to params["nx"],
                            "ny" to params["ny"]
                        )
                    )
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
    fun getRightNowWeather(
        params: Map<String, Any?> = mapOf()
    ) {

        addDisposable(
            weatherRepository.getRightNow(
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
                                    it.asJsonObject.asString("category") == "T1H" ||    // 기온
                                            it.asJsonObject.asString("category") == "RN1" ||    // 1시간 강수량
                                            it.asJsonObject.asString("category") == "SKY" ||    // 하늘 상태
                                            it.asJsonObject.asString("category") == "PTY"   // 강수형태
                            }

                        L.d("itemsJsonArray $itemsJsonArray")
                        _weatherRightNowJson.postValue(itemsJsonArray)
                    }
                }
                .doFinally{
                    _isLoading.postValue(false)
                }
                .subscribe()
        )

    }

    /**
     * 위젯 아이디 조회
     */
    fun getWidgetId() {
        addDisposable(
            widgetIdRepository.getWidgetId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { e ->
                    L.d("e : $e")
                }
                .doOnNext { list ->
                    L.d("list : $list")
                    _widgetIdList.postValue(list)
                }
                .subscribe()
        )
    }

    /**
     * 위젯 아이디 저장
     */
    fun saveWidgetId(vararg id: WidgetId) {
        addDisposable(
            widgetIdRepository.saveWidgetId(*id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { e ->

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
    fun getWeatherMid(
        params: Map<String, Any?> = mapOf()
    ) {
        _isLoading.postValue(true)
        addDisposable(
            weatherRepository.getWeatherMidTa(
                params = params
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { e ->
                    L.d("e : $e")
                }
                .doOnNext { json ->
                    L.d("json : $json")
                    if (isWeatherSuccess(json)){
                        _weatherMidTaJson.postValue(json)
                    }
                }
                .doFinally{
                    getWeatherMidFcst(
                        mapOf(
                            "serviceKey" to C.WeatherApi.API_KEY,
                            "dataType" to "JSON",
                            "pageNo" to "1",
                            "numOfRows" to "10",
                            "regId" to "11B00000",
                            "tmFc" to "202211080600"
                        )
                    )
                }
                .subscribe()
        )
    }

    /**
     * 기상청 중기 육상 예보 정보
     * @param ServiceKey  API KEY
     * @param dataType  JSON, XML
     * @param pageNo  페이지 번호
     * @param numOfRows  한 페이지 결과 수
     * @param regId  예보구역 코드 (11B10101 서울)
     * @param tmFc  발표시각 (일 2회 06:00 18:00 생성 YYYYMMDD0600(1800))
     */
    fun getWeatherMidFcst(
        params: Map<String, Any?> = mapOf()
    ) {
        addDisposable(
            weatherRepository.getWeatherMidFcst(
                params = params
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { e ->
                    L.d("e : $e")
                }
                .doOnNext { json ->
                    L.d("json : $json")
                    if (isWeatherSuccess(json)){
                        _weatherMidFcstJson.postValue(json)
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

    /**
     * 로컬에 저장된 날씨 데이터 조회
     */
    fun getLocalWeatherData() {
        addDisposable(
            weatherRepository.getLocalWeatherData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { e ->
                    L.d("e : $e")
                }
                .doOnNext { list ->
                    L.d("local weather list : $list")
                    _localWeatherNowDataList.postValue(list)
                }
                .subscribe()
        )
    }

    /**
     * 로컬에 초단기 날씨 데이터 갱신
     */
    fun saveLocalNowWeatherData(dataList: MutableList<WeatherNow>) {
        val saveLocalList = mutableListOf<WeatherNow>()
        addDisposable(
            weatherRepository.getLocalWeatherData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { e ->
                    L.d("e : $e")
                }
                .doOnNext { list ->
                    L.d("local weather list : $list")
                    list.forEach { localList ->
                        dataList.forEach{ rightNowList ->
                            if (localList.time == rightNowList.time){
                                saveLocalList.add(
                                    WeatherNow(
                                        time = localList.time,
                                        location = localList.location,
                                        pop = localList.pop,
                                        pty = localList.pty,
                                        tmn = localList.tmn,
                                        tmx = localList.tmx,
                                        sky = rightNowList.sky,
                                        tmp = rightNowList.tmp,
                                        pcp = rightNowList.pcp
                                    )
                                )
                            } else {
                                saveLocalList.add(localList)
                            }
                        }
                    }
                }
                .doFinally {
                    if (saveLocalList.size > 0) {
                        saveLocalWeatherData(saveLocalList)
                    }
                }
                .subscribe()
        )
    }

    /**
     * ROOM 로컬 날씨 데이터 저장
     */
    fun saveLocalWeatherData(data: List<JsonElement>, type: WeatherType) {
        // TODO chan 중복된 시간 데이터는 갱신 / 새로운 데이터는 추가,
        //  초단기, 단기 중기 데이터 꺼내오는 로직 분기처리 필요

        // TODO chan 임시로 초단기 데이터 테스트
        val dataList: MutableList<WeatherNow> = mutableListOf()

        // 시간 값 세팅
        val timeList: MutableList<Map<String, String>> = mutableListOf()
        data.forEach {
            val timeItem = mapOf(
                "fcstDate" to
                        it.asJsonObject.asString("fcstDate"),
                "fcstTime" to
                        it.asJsonObject.asString("fcstTime")
            )

            if (!timeList.contains(timeItem)) {
                timeList.add(timeItem)
            }
        }

        timeList.forEach { timeData ->
            var tmp = ""    // 1시간 기온
            var sky = ""    // 하늘
            var pty = ""    // 강수 형태
            var pcp = ""    // 1시간 강수량
            var pop = ""    // 강수 확률
            var tmn = ""    // 일 최저기온
            var tmx = ""    // 일 최고기온
            var location = // 위치 정보
                data[0].asJsonObject.asString("nx") + "." +
                        data[0].asJsonObject.asString("ny")


            data.forEach { data ->
                if (data.asJsonObject.asString("fcstDate") == timeData["fcstDate"] &&
                    data.asJsonObject.asString("fcstTime") == timeData["fcstTime"]) {

                    when(type) {
                        WeatherType.RIGHT_NOW -> {      // 초단기
                            // TODO chan 나머지 값들 유지 (ex pop 강수확률)  초단기 단기 중기 테이블을 따로 둬야하나
                            if (data.asJsonObject.asString("category") == "PTY") {
                                pty = data.asJsonObject.asString("fcstValue")
                            } else if (data.asJsonObject.asString("category") == "T1H") {
                                tmp = data.asJsonObject.asString("fcstValue")
                            } else if (data.asJsonObject.asString("category") == "SKY") {
                                sky = data.asJsonObject.asString("fcstValue")
                            } else if (data.asJsonObject.asString("category") == "RN1") {
                                pcp = data.asJsonObject.asString("fcstValue")
                            }
                        }
                        WeatherType.NOW -> {            // 단기
                            if (data.asJsonObject.asString("category") == "PTY") {
                                pty = data.asJsonObject.asString("fcstValue")
                            } else if (data.asJsonObject.asString("category") == "TMP") {
                                tmp = data.asJsonObject.asString("fcstValue")
                            } else if (data.asJsonObject.asString("category") == "SKY") {
                                sky = data.asJsonObject.asString("fcstValue")
                            } else if (data.asJsonObject.asString("category") == "PCP") {
                                pcp = data.asJsonObject.asString("fcstValue")
                            } else if (data.asJsonObject.asString("category") == "POP") {
                                pop = data.asJsonObject.asString("fcstValue")
                            } else if (data.asJsonObject.asString("category") == "TMN") {
                                tmn = data.asJsonObject.asString("fcstValue")
                            } else if (data.asJsonObject.asString("category") == "TMX") {
                                tmx = data.asJsonObject.asString("fcstValue")
                            }
                        }

                        else -> {}
                    }

                }
            }

            dataList.add(
                WeatherNow(
                    time = (timeData["fcstDate"] + timeData["fcstTime"]).toLong(),
                    tmp = tmp,
                    sky = sky,
                    pty = pty,
                    pcp = pcp,
                    pop = pop,
                    location = location,
                    tmn = tmn,
                    tmx = tmx
                )
            )
        }

        when(type) {
            WeatherType.RIGHT_NOW -> {      // 초단기
                saveLocalNowWeatherData(dataList)
            }
            WeatherType.NOW -> {            // 단기
                addDisposable(
                    weatherRepository.saveLocalWeatherData(*dataList.toTypedArray())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError { e ->
                            L.d("e : $e")
                        }
                        .subscribe()
                )
            }

            else -> {}
        }


    }

    private fun saveLocalWeatherData(data: MutableList<WeatherNow>) {
        addDisposable(
            weatherRepository.saveLocalWeatherData(*data.toTypedArray())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { e ->
                    L.d("e : $e")
                }
                .subscribe()
        )
    }

    /**
     * ROOM 로컬 지난 날씨 데이터 삭제
     */
    fun deleteBeforeLocalWeatherData(weatherNowDataList: MutableList<WeatherNow>) {
        val today = "YYYYMMdd".getTimeNow().toLong()
        val time = "HHmm".getTimeNow().toInt()
        val deleteDataList: MutableList<WeatherNow> = mutableListOf()

        weatherNowDataList.forEach {
            // TODO chan 날짜 비교 후 시간 비교 추가하여 지난시간 데이터 삭제
            if (it.time/10000 < today) {
                deleteDataList.add(it)
            } else if (it.time/10000 == today &&
                it.time%10000 < time-100) {
                deleteDataList.add(it)
            }
        }

        L.d("deleteLocalWeatherDataList : $deleteDataList")

        if(deleteDataList.size > 0) {
            addDisposable(
                weatherRepository.deleteLocalWeatherData(*deleteDataList.toTypedArray())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError { e ->
                        L.d("e : $e")
                    }
                    .doFinally {
                        getLocalWeatherData()
                    }
                    .subscribe()
            )
        }

    }

}
