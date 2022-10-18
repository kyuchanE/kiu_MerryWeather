package kiu.dev.merryweather.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.base.BaseViewModel
import kiu.dev.merryweather.listener.RequestSubscriber
import kiu.dev.merryweather.repository.WeatherRepository
import kiu.dev.merryweather.utils.L
import javax.inject.Inject

class MainViewModel (
    private val weatherRepository: WeatherRepository
) : BaseViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> get() = _isLoading

    private val _weatherJson = MutableLiveData<JsonObject>()
    val weatherJson : LiveData<JsonObject> get() = _weatherJson

    fun getWeather(
        params: Map<String, Any?> = mapOf()
    ) {
        _isLoading.postValue(true)
        addDisposable(
            weatherRepository.getWeather(
                params = params
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { e ->
                    L.d("e : $e")
                }
                .doOnNext { json ->
                    _weatherJson.postValue(json)
                    L.d("json : $json")
                }
                .doFinally{
                    _isLoading.postValue(false)
                }
                .subscribe()
        )

    }
}
