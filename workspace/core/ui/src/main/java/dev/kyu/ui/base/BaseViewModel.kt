package dev.kyu.ui.base

import androidx.lifecycle.ViewModel
import dev.kyu.ui.utils.dateToInt
import dev.kyu.ui.utils.dateToString
import java.util.Calendar
import java.util.Date

abstract class BaseViewModel: ViewModel() {

    companion object {
        const val ULTRA_WEATHER_TYPE = "ULTRA_WEATHER_TYPE"
        const val VILAGE_WEATHER_TYPE = "VILAGE_WEATHER_TYPE"

        const val MID_TA_TYPE = "MID_TA_TYPE"
        const val MID_LAND_FCST_TYPE = "MID_LAND_FCST_TYPE"
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
        calendar.add(Calendar.HOUR, -1)
        return calendar.time.dateToString("HH") + "00"
    }

    fun getMidBaseTime(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        return calendar.time.dateToString("yyyyMMdd") + "1800"
    }

    fun getMidBaseTime(addDayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, addDayOfMonth)
        return calendar.time.dateToString("yyyyMMdd") + "0000"
    }

    fun getMidLandFcstSkyStr(wfAm: String, wfPm: String): String {
        var wfAmSky = returnMidLandSky(wfAm.replace(" ", ""))
        var wfPmSky = returnMidLandSky(wfPm.replace(" ", ""))

        return if (wfAmSky > wfPmSky) wfAmSky else wfPmSky
    }

    private fun returnMidLandSky(str: String): String {
        return if (str.contains("맑음")) {
            "1"
        } else if (str.contains("구름많음") || str.contains("구름많고")) {
            "3"
        } else if (str.contains("흐림") || str.contains("흐리고")) {
            "4"
        }  else {
            "1"
        }
    }

    fun getMidLandFcstPtyStr(wfAm: String, wfPm: String): String {
        var wfAmPty = returnMidLandPty(wfAm.replace(" ", ""))
        var wfPmPty = returnMidLandPty(wfPm.replace(" ", ""))

        return if (wfAmPty > wfPmPty) wfAmPty else wfPmPty
    }

    private fun returnMidLandPty(str: String): String {
        return if (str.contains("비")) {
            "1"
        } else if (str.contains("비/눈")) {
            "2"
        } else if (str.contains("눈")) {
            "3"
        } else if (str.contains("소나기")) {
            "4"
        } else {
            "0"
        }
    }

}