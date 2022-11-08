package kiu.dev.merryweather.data.local.weather.now

import androidx.room.Entity
import androidx.room.PrimaryKey
import kiu.dev.merryweather.config.C
import java.io.Serializable


@Entity(tableName = C.RoomTableName.WEATHER_NOW)
data class WeatherNow(
    @PrimaryKey(autoGenerate = true)
    var time: Long = 0L,  // 시간
    var location: String = "",   // 위치 정보
    var pop: String = "",   // 강수 확률
    var pty: String = "",   // 강수형태 : 없음(0),비(1),비/눈(2), 눈(3), 소나기(4), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
    var pcp: String = "",   // 1시간 강수량
    var sky: String = "",   // 하늘 상태 :  맑음(1), 구름많음(3), 흐림(4)
    var tmp: String = "",   // 1시간 기온
    var tmn: String = "",   // 일 최저기온
    var tmx: String = "",   // 일 최고기온
): Serializable
