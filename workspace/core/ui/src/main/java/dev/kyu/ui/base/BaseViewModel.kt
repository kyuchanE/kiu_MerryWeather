package dev.kyu.ui.base

import androidx.lifecycle.ViewModel
import dev.kyu.ui.utils.L
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

abstract class BaseViewModel: ViewModel() {

    companion object {
        const val ULTRA_WEATHER_TYPE = "ULTRA_WEATHER_TYPE"
        const val VILAGE_WEATHER_TYPE = "VILAGE_WEATHER_TYPE"
    }

    fun getBaseDate(): String = Date(System.currentTimeMillis()).dateToString("yyyyMMdd")

    fun getBaseTime(): String {
        return try {
            var hour = Date(System.currentTimeMillis()).dateToString("HH")
            var minutes = Date(System.currentTimeMillis()).dateToString("mm")
            if (minutes.toInt() <= 30) {
                if (hour == "00") {
                    hour = "00"
                    minutes = "00"
                } else {
                    hour = String.format("%02d", (hour.toInt() - 1))
                    minutes = "59"
                }
            }
            hour + minutes
        } catch (e: Exception) {
            "0900"
        }
    }

    fun getUltraBaseTime(): String {
        return try {
            var hour = Date(System.currentTimeMillis()).dateToString("HH")
            var minutes = Date(System.currentTimeMillis()).dateToString("mm")
            if (minutes.toInt() <= 30) {
                if (hour == "00") {
                    hour = "00"
                    minutes = "00"
                } else {
                    hour = String.format("%02d", (hour.toInt() - 1))
                    minutes = "59"
                }
            }
            hour + minutes
        } catch (e: Exception) {
            "0900"
        }
    }

    fun getVilageBaseTime(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, 1)
        return calendar.time.dateToString("yyyyMMddHH") + "00"
    }

    fun Date.dateToString(format: String): String {
        val simpleDateFormat = SimpleDateFormat(format)
        return simpleDateFormat.format(this)
    }
}