plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("com.google.dagger.hilt.android")
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.uber"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.uber"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        debug {
            val MAPBOX_DOWNLOADS_TOKEN = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").get()
            val GOOGLE_API_KEY = providers.gradleProperty("GOOGLE_API_KEY").get()
            buildConfigField("String","MAPBOX_TOKEN","\"${MAPBOX_DOWNLOADS_TOKEN}\"")
            buildConfigField("String","GOOGLE_API_KEY","\"${GOOGLE_API_KEY}\"")

        }
        release {
            val MAPBOX_DOWNLOADS_TOKEN = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").get()
            val GOOGLE_API_KEY = providers.gradleProperty("GOOGLE_API_KEY").get()
            buildConfigField("String","MAPBOX_TOKEN","\"${MAPBOX_DOWNLOADS_TOKEN}\"")
            buildConfigField("String","GOOGLE_API_KEY","\"${GOOGLE_API_KEY}\"")
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
    viewBinding{
        enable = true
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    var nav_version = "2.3.5"
    var room_version = "2.2.3"


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.karumi:dexter:6.2.3")

// Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation("androidx.fragment:fragment-ktx:1.5.2")



    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.0.6")
    implementation ("com.google.android.gms:play-services-location:17.0.0")
//    implementation(libs.mapbox.android.navigation.ui)
//    implementation (libs.mapbox.android.navigation)


//    implementation ("com.mapbox.mapboxsdk:mapbox-android-sdk:8.6.2")
    implementation ("com.mapbox.maps:android:11.7.1")
    implementation ("com.mapbox.navigationcore:navigation:3.4.1")
    implementation ("com.mapbox.navigationcore:ui-components:3.4.1")
    implementation ("com.mapbox.plugin:maps-locationcomponent:11.7.1")


    implementation ("com.google.android.gms:play-services-location:17.0.0")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")

    implementation("androidx.appcompat:appcompat")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta4")

    implementation ("com.android.support:design:27.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

    implementation ("com.squareup.retrofit2:retrofit:2.7.2")
    implementation ("com.squareup.retrofit2:converter-gson:2.7.2")

    val lifecycle_version = "2.6.2"
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    implementation("com.google.dagger:hilt-android:2.46.1")

    kapt ("com.google.dagger:hilt-compiler:2.46.1")

    implementation ("androidx.room:room-runtime:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")



}


