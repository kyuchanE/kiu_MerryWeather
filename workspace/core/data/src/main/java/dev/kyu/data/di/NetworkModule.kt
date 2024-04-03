package dev.kyu.data.di

import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.kyu.data.utils.NetworkConfig
import okhttp3.ConnectionSpec
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val PRINT_LOG = true // 로그 출력여부

    private const val CONNECT_TIMEOUT = 3000L // 커넥션 타임
    private const val WRITE_TIMEOUT = 3000L // 쓰기 타임
    private const val READ_TIMEOUT = 3000L // 읽기 타임

    private fun getLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        if (PRINT_LOG) {
            OkHttpClient.Builder()
//                .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS)) // https 관련 보안 옵션
                .cookieJar(JavaNetCookieJar(CookieManager()))       // 쿠키 매니저 연결
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)  // 쓰기 타임아웃 시간 설정
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)      // 읽기 타임아웃 시간 설정
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)        // 연결 타임아웃 시간 설정
                .cache(null)                                 // 캐시사용 안함
                .addInterceptor { chain ->
                    chain.proceed(
                        chain.request()
                            .newBuilder()
//                        .header("User-Agent", "")
//                        .header("devicemodel", Build.MODEL)
//                                    .header("key", "value")
                            .build()
                    )
                }
                .addInterceptor(getLoggingInterceptor())
                .addInterceptor(OkHttpProfilerInterceptor())
                .build()

        } else {
            OkHttpClient.Builder()
//                .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS)) // https 관련 보안 옵션
                .cookieJar(JavaNetCookieJar(CookieManager()))       // 쿠키 매니저 연결
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)  // 쓰기 타임아웃 시간 설정
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)      // 읽기 타임아웃 시간 설정
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)        // 연결 타임아웃 시간 설정
                .cache(null)                                 // 캐시사용 안함
                .addInterceptor { chain ->
                    chain.proceed(
                        chain.request()
                            .newBuilder()
//                        .header("User-Agent", "")
//                        .header("devicemodel", Build.MODEL)
//                                    .header("key", "value")
                            .build()
                    )
                }
                .build()

        }

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideWeatherRetrofitInstance(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(NetworkConfig.Weather.weatherBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())      // ScalarConverter 적용
        .addConverterFactory(gsonConverterFactory)                  // GsonConverter 적용
        .build()

}