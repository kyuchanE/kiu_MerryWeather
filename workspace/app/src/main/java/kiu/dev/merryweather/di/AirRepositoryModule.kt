package kiu.dev.merryweather.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kiu.dev.merryweather.data.AirApi
import kiu.dev.merryweather.data.BasicApi
import kiu.dev.merryweather.data.repository.AirRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
object AirRepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideAirRepository(
        airApi: AirApi
    ) = AirRepository(airApi)
}