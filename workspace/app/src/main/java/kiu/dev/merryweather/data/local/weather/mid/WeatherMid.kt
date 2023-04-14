package kiu.dev.merryweather.data.local.weather.mid

import androidx.room.Entity
import androidx.room.PrimaryKey
import kiu.dev.merryweather.config.C
import java.io.Serializable

@Entity(tableName = C.RoomTableName.WEATHER_MID)
data class WeatherMid(
    @PrimaryKey(autoGenerate = true)
    var date: Long = 0L,
    var tmn: String = "",   // 일 최저기온
    var tmx: String = "",   // 일 최고기온
    var amPop: String = "",   // 강수 확률
    var pmPop: String = "",
    var amPty: String = "",   // 강수형태 : 없음(0),비(1),비/눈(2), 눈(3), 소나기(4), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
    var pmPty: String = "",
    var amSky: String = "",   // 하늘 상태 :  맑음(1), 구름많음(3), 흐림(4)
    var pmSky: String = ""
): Serializable