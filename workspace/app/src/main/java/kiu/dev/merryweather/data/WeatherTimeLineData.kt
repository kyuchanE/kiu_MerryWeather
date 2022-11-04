package kiu.dev.merryweather.data

import android.graphics.drawable.Drawable

data class WeatherTimeLineData(
    var date: String,
    var time: String,
    var drawable: Drawable,
    var temperature: String
)