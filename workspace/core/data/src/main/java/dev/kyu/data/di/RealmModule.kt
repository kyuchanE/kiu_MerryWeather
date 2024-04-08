package dev.kyu.data.di

import dagger.Module
import dagger.Provides
import dev.kyu.domain.model.WeatherData
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
object RealmModule {
    var mRealm: Realm? = null

    @Provides
    @Singleton
    fun provideRealm(): Realm =
        if (mRealm == null) {
            val realmConfig = RealmConfiguration.create(schema = setOf(WeatherData::class))
            mRealm = Realm.open(realmConfig)
            Realm.open(realmConfig)
        } else {
            mRealm!!
        }

}