package kiu.dev.merryweather.repository

import com.google.gson.JsonObject
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.model.BasicApi
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val baseApi: BasicApi
) {

    fun getWeather(
        params: Map<String, Any?> = mapOf(),
        header: Map<String, Any?> = mapOf()
    ): Flowable<JsonObject> {
        return baseApi.getApi(
            url = "",
            params,
            header
        )
    }
}