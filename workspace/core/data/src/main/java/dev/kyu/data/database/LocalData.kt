package dev.kyu.data.database

import io.realm.kotlin.Realm
import javax.inject.Inject

class LocalData @Inject constructor(
    private val realmDb: Realm
) {

}