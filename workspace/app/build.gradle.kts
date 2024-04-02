plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "kiu.dev.merryweather"
    compileSdk = SdkVersions.compileSdk

    defaultConfig {
        applicationId = "kiu.dev.merryweather"
        minSdk = SdkVersions.minSdk
        targetSdk = SdkVersions.targetSdk
        versionCode = AppVersions.androidVersionCode
        versionName = AppVersions.androidVersionName

        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            // 개발여부설정 : false
            buildConfigField ("boolean", "DEV", "false")
        }
        getByName("debug") {
            // 개발여부설정 : true
            buildConfigField("boolean", "DEV", "true")
        }
    }

//    flavorDimensions 'default'
//    productFlavors {
//        def CONFIG = { k -> "\"${project.properties.get(k)}\"" }
//        dev {
//            // 디버그앱 이름 설정
//            manifestPlaceholders = [appLabel: "_DEV"]
//            buildConfigField "String", "WEATHER_BASE_URL", CONFIG("weather.base.url")
//        }
//
//        rel {
//            // 디버그앱 이름 설정
//            manifestPlaceholders = [appLabel: "_REL"]
//            buildConfigField "String", "WEATHER_BASE_URL", CONFIG("weather.base.url")
//        }
//    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":feature:main"))
    implementation(project(":feature:weather"))
    implementation(project(":feature:setting"))

    implementation(Dependency.KTX.CORE)
    implementation(Dependency.AndroidX.APP_COMPAT)
    implementation(Dependency.Google.MATERIAL)
    implementation(Dependency.AndroidX.CONSTRAINT_LAYOUT)
    testImplementation(Dependency.Test.JUNIT)
    androidTestImplementation(Dependency.AndroidTest.EXT_JUNIT)
    androidTestImplementation(Dependency.AndroidTest.ESPRESSO_CORE)

    // gson
    implementation(Dependency.Gson.GSON)
    // okhttp
    implementation(Dependency.OkHttp.OKHTTP)
    implementation(Dependency.OkHttp.URL_CONNECTION)
    implementation(Dependency.OkHttp.LOGGER_INTERCEPTOR)
    implementation(Dependency.OkHttp.OKHTTP_PROFILER)
    // retrofit2
    implementation(Dependency.Retrofit.RETROFIT)
    implementation(Dependency.Retrofit.CONVERTER_GSON)
    implementation(Dependency.Retrofit.CONVERTER_SCALARS)
    implementation(Dependency.Retrofit.CONVERTER_JAXB)
    // logger
    implementation(Dependency.Logger.LOGGER)
    // splash
    implementation(Dependency.Splash.SPLASH)
    // dagger hilt
    implementation(Dependency.DaggerHilt.DAGGER_HILT)
    kapt(Dependency.DaggerHilt.DAGGER_HILT_COMPILER)
    kapt(Dependency.DaggerHilt.DAGGER_HILT_ANDROIDX_COMPILER)
    // ViewModel
    implementation(Dependency.AndroidX.LIFECYCLE_VIEW_MODEL)
    implementation(Dependency.AndroidX.ACTIVITY)
    implementation(Dependency.AndroidX.FRAGMENT)
    // LiveData
    implementation(Dependency.AndroidX.LIFECYCLE_LIVEDATA)
    // room
//    implementation "androidx.room:room-runtime:2.4.3"
//    implementation "androidx.room:room-ktx:2.4.3"
//    implementation "androidx.room:room-rxjava2:2.4.3"
//    implementation "androidx.room:room-rxjava3:2.4.3"
//    annotationProcessor "androidx.room:room-compiler:2.4.3"
//    kapt 'androidx.room:room-compiler:2.4.3'

    // swiperefreshlayout
    implementation(Dependency.SwiperRefreshLayout.SWIPER_REFRESH)
    // glide
    kapt(Dependency.Glide.GLIDE_COMPILER)
    implementation(Dependency.Glide.GLIDE_OKHTTP_INTEGRATION)
    implementation(Dependency.Glide.GLIDE_TRANSFORMATIONS)
    // WorkManager
    implementation(Dependency.WorkManager.WORK_RUNTIME_KTX)
    // firebase
    implementation(Dependency.Firebase.FIREBASE_ANALYTICS)
    implementation(Dependency.Firebase.FIREBASE_DATABASE)
    // Coroutines
    implementation(Dependency.Coroutines.COROUTINES)
}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}