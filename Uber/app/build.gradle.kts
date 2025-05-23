plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
    id("com.apollographql.apollo") version "4.1.1"
    id("kotlin-parcelize")
}
secrets {
    // To add your Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
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
            val SESSION_TOKEN = providers.gradleProperty("SESSION_TOKEN").get()
            buildConfigField("String", "MAPBOX_TOKEN", "\"${MAPBOX_DOWNLOADS_TOKEN}\"")
            buildConfigField("String", "GOOGLE_API_KEY", "\"${GOOGLE_API_KEY}\"")
            buildConfigField("String", "SESSION_TOKEN", "\"${SESSION_TOKEN}\"")


        }
        release {
            val MAPBOX_DOWNLOADS_TOKEN = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").get()
            val GOOGLE_API_KEY = providers.gradleProperty("GOOGLE_API_KEY").get()
            val SESSION_TOKEN = providers.gradleProperty("SESSION_TOKEN").get()
            buildConfigField("String", "MAPBOX_TOKEN", "\"${MAPBOX_DOWNLOADS_TOKEN}\"")
            buildConfigField("String", "GOOGLE_API_KEY", "\"${GOOGLE_API_KEY}\"")
            buildConfigField("String", "SESSION_TOKEN", "\"${SESSION_TOKEN}\"")
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
    viewBinding {
        enable = true
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation("com.google.android.libraries.places:places:3.2.0")
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.core.animation)
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
    implementation("com.karumi:dexter:6.2.3")

// Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation("androidx.fragment:fragment-ktx:1.5.2")



    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.0.6")
    implementation("com.google.android.gms:play-services-location:17.0.0")

    implementation("io.reactivex.rxjava3:rxjava:3.1.0")
    implementation("com.jakewharton.rxbinding:rxbinding:0.4.0")


    implementation(libs.shimmer)




    implementation("com.google.android.gms:play-services-location:17.0.0")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")

    implementation("androidx.appcompat:appcompat")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta4")

    implementation("com.android.support:design:27.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

    implementation("com.squareup.retrofit2:retrofit:2.7.2")
    implementation("com.squareup.retrofit2:converter-gson:2.7.2")

    val lifecycle_version = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    implementation("com.google.dagger:hilt-android:2.46.1")

    kapt("com.google.dagger:hilt-compiler:2.46.1")

    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    implementation("com.google.android.gms:play-services-maps:19.0.0")

    implementation("com.github.amalChandran:trail-android:v1.51")
    implementation("com.google.maps.android:android-maps-utils:3.9.0")
    implementation("com.github.tintinscorpion:Dual-color-Polyline-Animation:1.2")
    implementation("com.github.gsanthosh91:Google-Map-Floating-Info-Window-Uber:0.1.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.microsoft.signalr:signalr:7.0.0")
    implementation(libs.countrycodepicker)

    implementation("com.github.garretyoder:Colorful:2.3.4")


    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation("com.google.android.gms:play-services-auth:21.3.0")

    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-measurement-api:22.2.0")

    implementation("com.apollographql.apollo:apollo-runtime:4.1.1")
    implementation("com.mobsandgeeks:android-saripaar:2.0.3")

    implementation("io.michaelrocks:libphonenumber-android:8.12.44")
    implementation("com.github.joielechong:countrycodepicker:2.4.2")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    kapt ("com.github.bumptech.glide:compiler:4.16.0")

    implementation("org.conscrypt:conscrypt-android:2.2.1")

    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.16.0")

    implementation ("com.blankj:utilcodex:1.31.1")

}


