package kiu.dev.merryweather.data.local

import android.graphics.drawable.Drawable
import java.time.DayOfWeek

data class WeatherWeekLineData(
    var dayOfWeek: String,
    var drawable: Drawable?,
    var temperature: String,
    var pop: String
)