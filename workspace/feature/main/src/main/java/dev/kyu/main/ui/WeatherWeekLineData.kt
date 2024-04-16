package dev.kyu.main.ui

import android.graphics.drawable.Drawable

data class WeatherWeekLineData(
    var date: String = "",
    var sky: String = "",       // 하늘 상태
    var tmn: String = "",       // 일 최저 기온
    var tmx: String = "",       // 일 최고 기온
    var pty: String = "",       // 강수 형태
    var pop: String,            // 강수 확률
)
