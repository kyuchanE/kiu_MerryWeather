package dev.kyu.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class WeatherData(): RealmObject {
    var dateTime: String = ""
    // TODO chan Change Name
    var t1h: String = ""
    var sky: String = ""
    var rn1: String = ""
    var pty: String = ""
}