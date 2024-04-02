plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "dev.kyu.main"
    compileSdk = SdkVersions.compileSdk

    defaultConfig {
        minSdk = SdkVersions.minSdk
        targetSdk = SdkVersions.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
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
    }
}

dependencies {

    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":feature:weather"))
    implementation(project(":feature:setting"))

    implementation(Dependency.KTX.CORE)
    implementation(Dependency.AndroidX.APP_COMPAT)
    implementation(Dependency.Google.MATERIAL)
    implementation(Dependency.AndroidX.CONSTRAINT_LAYOUT)
    testImplementation(Dependency.Test.JUNIT)
    androidTestImplementation(Dependency.AndroidTest.EXT_JUNIT)
    androidTestImplementation(Dependency.AndroidTest.ESPRESSO_CORE)

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
    // firebase
    implementation(Dependency.Firebase.FIREBASE_ANALYTICS)
    implementation(Dependency.Firebase.FIREBASE_DATABASE)
    // Coroutines
    implementation(Dependency.Coroutines.COROUTINES)
    // swiperefreshlayout
    implementation(Dependency.SwiperRefreshLayout.SWIPER_REFRESH)
    // glide
    kapt(Dependency.Glide.GLIDE_COMPILER)
    implementation(Dependency.Glide.GLIDE_OKHTTP_INTEGRATION)
    implementation(Dependency.Glide.GLIDE_TRANSFORMATIONS)
    // WorkManager
    implementation(Dependency.WorkManager.WORK_RUNTIME_KTX)

}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}