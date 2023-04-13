package kiu.dev.merryweather.data.repository

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.data.AirApi
import kiu.dev.merryweather.di.NetworkModule
import kiu.dev.merryweather.utils.asJsonArray
import kiu.dev.merryweather.utils.asJsonObject
import javax.inject.Inject


class AirRepository @Inject constructor(
    private val airApi: AirApi
) {

    /**
     * 시도별 실시간 대기오염 정보 조회
     */
    fun getCityAir(
        params: Map<String, Any?> = mapOf(),
        header: Map<String, Any?> = mapOf()
    ): Flowable<JsonArray> {
        return airApi.getApi(
            url = C.AirApi.CITY_AIR,
            params,
            header
        )
            .subscribeOn(Schedulers.io())
            .flatMap(::getItems)
            .observeOn(AndroidSchedulers.mainThread())
    }

//    private fun isWeatherSuccess(data: JsonObject): Boolean {
//
//    }

    private fun getItems(data: JsonObject): Flowable<JsonArray> =
        Flowable
            .fromArray(
                data.asJsonObject("response")
                    .asJsonObject("body")
                    .asJsonArray("items")
            )
            .subscribeOn(Schedulers.io())
            .doOnError {  }
            .doOnNext {  }
            .doFinally {  }
}