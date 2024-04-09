package dev.kyu.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class WeatherData(): RealmObject {
    var dateTime: String = ""
}