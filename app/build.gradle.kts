plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "it.post.app"
    compileSdk = 33

    defaultConfig {
        applicationId = "it.post.app"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.constraintLayout)
    implementation(Google.android.material)

    implementation(AndroidX.dataStore.preferences)

    implementation(AndroidX.navigation.fragmentKtx)
    implementation(AndroidX.navigation.uiKtx)

    implementation(AndroidX.swipeRefreshLayout)

    implementation(Square.moshi)
    implementation(Square.moshi.adapters)
    kapt(Square.moshi.kotlinCodegen)

    implementation(Square.retrofit2.converter.moshi)
    implementation(Square.retrofit2.retrofit)

    implementation(libs.epoxy)
    implementation(libs.epoxy.databinding)
    kapt(libs.epoxy.processor)

    implementation(libs.fluentui.persona)
    implementation(libs.glide)
    implementation(libs.imagePicker)
    implementation(libs.kotlinResult)
    implementation(libs.networkResponseAdapter)
    implementation(libs.toasty)

    testImplementation(Testing.junit4)
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.espresso.core)
}
