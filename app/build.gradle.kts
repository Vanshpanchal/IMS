plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
//    id("com.google.dagger.hilt.android")
//    id ("kotlin-kapt")
}

android {
    namespace = "com.example.ims"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ims"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
//    kapt {
//        correctErrorTypes = true
//    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.places)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.3.0-beta01")
    implementation("com.google.android.material:material:1.5.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.google.code.gson:gson:2.8.8")
    implementation("androidx.core:core-splashscreen:1.0.0")
    val lottieVersion = "3.4.0"
    implementation ("com.airbnb.android:lottie:$lottieVersion")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.libraries.places:places:3.1.0")
//    implementation ("org.mongodb:mongodb-driver-sync:4.7.1")
//    implementation ("org.litote.kmongo:kmongo:4.5.1" )        // KMongo
//    kapt(libs.hilt.android.compiler)
//
//    implementation ("com.google.dagger:hilt-android:2.38.1")


    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0-beta01")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.0-beta01")

//    kapt ("com.google.dagger:hilt-compiler:2.38.1")
//    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.android.volley:volley:1.2.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

}
