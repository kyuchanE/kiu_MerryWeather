package dev.kyu.weather.model

import android.graphics.drawable.Drawable

data class WeatherWeekLineData(
    var dayOfWeek: String,
    var amDrawable: Drawable?,
    var pmDrawable: Drawable?,
    var tmn: String = "",   // 일 최저기온
    var tmx: String = "",   // 일 최고기온
    var pop: String
)
