package kiu.dev.merryweather.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kiu.dev.merryweather.base.BaseViewModel
import kiu.dev.merryweather.utils.L
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class TestViewModel : BaseViewModel(){

    private val _isTestLoading = MutableLiveData<Boolean>()
    val isTestLoading: LiveData<Boolean> get() = _isTestLoading

    private val _liveData =  MutableLiveData(100)
    val liveData: LiveData<Int> = _liveData

    private val _showToastEvent = MutableSharedFlow<String>()
    val showToastEvent = _showToastEvent.asSharedFlow()

    fun getTestData() {
        _isTestLoading.postValue(true)
        L.d("getTestData ")
    }

    fun changeLiveData() {
        L.d("TestViewModel changeLiveData() ")
        viewModelScope.launch {
            repeat(10) {
                _liveData.value = _liveData.value?.plus(1)
                delay(1000L)
            }
        }
    }

    fun changeSharedFlow() {
        viewModelScope.launch {
            _showToastEvent.emit("ChangeSharedFlow!!")
        }
    }
}