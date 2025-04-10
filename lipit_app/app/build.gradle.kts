import java.io.FileInputStream
import java.util.Properties

// local.properties 읽기
val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}
val serverUrl_spring = localProperties["SERVER_URL_SPRING"] as String
val serverUrl_fastapi = localProperties["SERVER_URL_FASTAPI"] as String
val open_ai_key = localProperties["OPEN_AI_KEY"] as String

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.ssafy.lipit_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ssafy.lipit_app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "SERVER_URL_SPRING", "\"$serverUrl_spring\"")
            buildConfigField("String", "SERVER_URL_FASTAPI", "\"$serverUrl_fastapi\"")
            buildConfigField("String", "OPEN_AI_KEY", "\"$open_ai_key\"")
        }

        getByName("release") {
            buildConfigField("String", "SERVER_URL_SPRING", "\"$serverUrl_spring\"")
            buildConfigField("String", "SERVER_URL_FASTAPI", "\"$serverUrl_fastapi\"")
            buildConfigField("String", "OPEN_AI_KEY", "\"$open_ai_key\"")
        }

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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
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
    implementation(libs.gms.play.services.wearable)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.testing)
    implementation(libs.androidx.camera.view)
    implementation(libs.gms.play.services.wearable)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.compose.testing)
    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.0")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:1.7.8")

    implementation("androidx.compose.material:material:1.7.5")     // material2 지원
    implementation("com.kizitonwose.calendar:compose:2.6.2")
    implementation("androidx.compose.material:material:1.7.5")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation ("androidx.navigation:navigation-compose:2.8.5")  // 네비게이션 구현

    implementation("androidx.compose.foundation:foundation:1.7.7") // LazyGrid 지원
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0") // 날짜 지원

    // retrofit2
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    // Compose ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // fcm
    implementation(platform("com.google.firebase:firebase-bom:33.8.0")) // 최신 버전 유지
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")

    // okhttp3
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // datastore
    implementation ("androidx.datastore:datastore-preferences:1.1.2")
    implementation ("androidx.datastore:datastore-core:1.1.2")  // 코어 의존성 (필요 시)

    // lottie
    implementation ("com.airbnb.android:lottie-compose:6.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation ("androidx.navigation:navigation-compose:2.4.0-alpha10") //네비게이션

    // datastore
    implementation ("androidx.datastore:datastore-preferences:1.1.2")
    implementation ("androidx.datastore:datastore-core:1.1.2")  // 코어 의존성 (필요 시)

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation ("androidx.navigation:navigation-compose:2.4.0-alpha10") //네비게이션
    implementation("androidx.compose.material:material:1.5.4")


    implementation("androidx.camera:camera-camera2:1.4.0-alpha02")
    implementation("androidx.camera:camera-lifecycle:1.4.0-alpha02")
    implementation ("androidx.camera:camera-view:1.4.0-alpha02")

    // workmanager
    implementation ("androidx.work:work-runtime-ktx:2.7.1")

    // location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // indicator
    implementation("mx.platacard:compose-pager-indicator:0.0.8")

    // ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.6.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.6.0")
    implementation("androidx.media3:media3-ui:1.6.0")


    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.compose.ui:ui-tooling:1.5.1") // Preview용
    implementation("androidx.compose.ui:ui:1.5.1")

    // url을 통해 이미지를 불러오기 위한 이미지 라이브러리 - Coil
    implementation("io.coil-kt:coil-compose:2.4.0")

    //바텀시트 구현 관련
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))

    //FCM 관련
    apply(plugin = "com.google.gms.google-services")

    // WebSocket
    implementation("org.java-websocket:Java-WebSocket:1.5.3")

    // 그리드 리스트 화면
    implementation("com.cheonjaeung.compose.grid:grid:2.2.1")

    // 로딩화면 관련
    implementation("com.airbnb.android:lottie-compose:6.0.0")

    // 상태바 색상 변경 관련
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.3-beta")
    // 음성파일 병합 위한 라이브러리
    implementation("com.arthenica:ffmpeg-kit-full:6.0")

    implementation ("androidx.compose.foundation:foundation-layout:1.3.1")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.33.2-alpha")

}

