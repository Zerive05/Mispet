plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt") // <-- Ubah menjadi seperti ini
}

android {
    namespace = "com.abadi.mispet"
    // Ganti ke versi SDK yang stabil
    compileSdk = 36

    defaultConfig {
        applicationId = "com.abadi.mispet"
        minSdk = 24
        // Ganti ke versi SDK yang stabil
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
        // Sesuaikan dengan Java versi 17 untuk SDK 34
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        // Sesuaikan dengan Java versi 17
        jvmTarget = "17"
    }
}

dependencies {
    // Pastikan semua dependensi menggunakan format implementation(...)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Dependensi untuk Room (sintaks sudah benar di sini)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // Opsional - Dukungan untuk Kotlin Coroutines
    implementation("androidx.room:room-ktx:$room_version")

    implementation("androidx.recyclerview:recyclerview:1.3.2")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
}
