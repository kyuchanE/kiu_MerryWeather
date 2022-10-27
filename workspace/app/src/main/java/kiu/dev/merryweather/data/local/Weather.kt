package kiu.dev.merryweather.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kiu.dev.merryweather.config.C
import java.io.Serializable


@Entity(tableName = C.RoomTableName.WEATHER_ITEM)
data class Weather(
    @PrimaryKey(autoGenerate = true)
    var time: Long = 0L,  // 시간
    var location: String = "",   // 위치 정보
    var pop: String = "",   // 강수 확률
    var pty: String = "",   // 강수형태
    var pcp: String = "",   // 1시간 강수량
    var sky: String = "",   // 하늘 상태
    var tmp: String = "",   // 1시간 기온
    var tmn: String = "",   // 일 최저기온
    var tmx: String = "",   // 일 최고기온
): Serializable

// TODO chan 지나간 날씨 데이터 삭제 로직 필요