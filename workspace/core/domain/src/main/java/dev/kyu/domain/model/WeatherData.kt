package dev.kyu.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class WeatherData(): RealmObject {
    var dateTime: String = ""
    var t1h: String = ""                // 1시간 기온
    var sky: String = ""                // 하늘 상태
    var rn1: String = ""                // 1시간 강수량
    var pty: String = ""                // 강수 형태
    var reh: String = ""                // 습도
    var pop: String = ""                // 강수 확률
    var tmn: String = ""                // 일 최저 기온
    var tmx: String = ""                // 일 최고 기온
}