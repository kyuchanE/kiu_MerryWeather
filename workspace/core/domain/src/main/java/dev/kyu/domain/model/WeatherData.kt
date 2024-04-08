package dev.kyu.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class WeatherData(): RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var dateTime: String = ""

    constructor(dateTime: String = ""): this() {
        this.dateTime = dateTime
    }
}