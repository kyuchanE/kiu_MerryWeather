package kiu.dev.merryweather.data.local

import android.graphics.drawable.Drawable
import java.time.DayOfWeek

data class WeatherWeekLineData(
    var dayOfWeek: String,
    var amDrawable: Drawable?,
    var pmDrawable: Drawable?,
    var tmn: String = "",   // 일 최저기온
    var tmx: String = "",   // 일 최고기온
    var pop: String
)