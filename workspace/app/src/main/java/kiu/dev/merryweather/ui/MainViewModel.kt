package kiu.dev.merryweather.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kiu.dev.merryweather.base.BaseViewModel
import kiu.dev.merryweather.repository.WeatherRepository
import javax.inject.Inject



@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
)  : BaseViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    private val isLoading : LiveData<Boolean> get() = _isLoading

    private val _weatherJson = MutableLiveData<JsonObject>()
    private val weatherJson : LiveData<JsonObject> get() = _weatherJson

    fun getWeather(
        params: Map<String, Any?> = mapOf()
    ) {

        addDisposable(
            weatherRepository.getWeather(
                params = params
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { e ->

                }
                .doOnNext { json ->

                }
                .doFinally{

                }
                .subscribe()
        )

    }
}