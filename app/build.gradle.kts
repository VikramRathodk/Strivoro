plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.parcelize)

    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.firebase.firebase.perf)


}

android {
    namespace = "com.devvikram.striveo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.devvikram.striveo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)


    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.firebase.auth.ktx)

    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    //dagger hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)


    // Ksp
    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.android.compiler)

    // Kotlin serialization
    implementation(libs.kotlinx.serialization.json)

    // firebase dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)

    // retro and gson
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)


    // navigation
    implementation(libs.androidx.navigation.compose)

    // lottie
    implementation(libs.lottie.compose)


    // google font
    implementation(libs.androidx.ui.text.google.fonts)

    // coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    //
    implementation(libs.androidx.constraintlayout.compose)
    implementation (libs.google.accompanist.systemuicontroller)


    implementation("androidx.compose.material:material-icons-extended:1.7.0")

    implementation(libs.androidx.datastore)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}