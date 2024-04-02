package dev.kyu.data.api

import dev.kyu.data.utils.NetworkConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET(NetworkConfig.weatherShort)
    suspend fun getTest(
        @Query("tmfc") tmfc: Int,
        @Query("authKey") authKey: String,
    )

}