package dev.kyu.domain.repository

import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun getFctShort(): Flow<JsonObject?>
}