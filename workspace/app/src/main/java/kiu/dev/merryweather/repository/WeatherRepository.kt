package kiu.dev.merryweather.repository

import com.google.gson.JsonObject
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.model.BasicApi
import org.koin.core.component.KoinComponent
import javax.inject.Inject

class WeatherRepository (
    private val baseApi: BasicApi
): KoinComponent {

    fun getWeather(
        params: Map<String, Any?> = mapOf(),
        header: Map<String, Any?> = mapOf()
    ): Flowable<JsonObject> {
        return baseApi.getApi(
            url = "VilageFcstInfoService_2.0/getVilageFcst",
            params,
            header
        )
    }
}