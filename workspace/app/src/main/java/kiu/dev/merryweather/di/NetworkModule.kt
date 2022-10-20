package kiu.dev.merryweather.di

import android.annotation.SuppressLint
import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import kiu.dev.merryweather.config.C
import kiu.dev.merryweather.model.BasicApi
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object NetworkModule {
    private const val PRINT_LOG = true // 로그 출력여부

    private const val CONNECT_TIMEOUT = 3000L // 커넥션 타임
    private const val WRITE_TIMEOUT = 3000L // 쓰기 타임
    private const val READ_TIMEOUT = 3000L // 읽기 타임

    private val BASE_URL = C.WeatherApi.BASE_URL // API URL

    lateinit var context: Context

    val okHttpClient by lazy {
        val okHttpClientBuilder = OkHttpClient.Builder()

        okHttpClientBuilder
//            .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS)) // https 관련 보안 옵션
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

        if (PRINT_LOG) {
            val httpLoggingInterceptor = LoggingInterceptor.Builder() // 전송로그
                .setLevel(Level.BASIC)
                .log(Platform.INFO)
                .tag("log")
                .build()

            okHttpClientBuilder
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(StethoInterceptor()) // Stetho 로그
        }

        okHttpClientBuilder.build()
    }

    val basicApi by lazy { build().create(BasicApi::class.java) }

    fun init(ctx: Context) {
        context = ctx
    }

    /**
     * 기본 설정하여 Retrofit을 반환
     *
     * @return 설정이 반영된 Retrofit
     */
    fun build(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // Rx를 사용할 수 있도록 아답터 적용
            .addConverterFactory(ScalarsConverterFactory.create())      // ScalarConverter 적용
            .addConverterFactory(GsonConverterFactory.create())         // GsonConverter 적용
            .build()
    }
}