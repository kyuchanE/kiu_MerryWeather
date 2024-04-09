package dev.kyu.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.kyu.domain.model.WeatherData
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
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