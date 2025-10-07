plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") // Plugin de Compose Compiler requerido para Kotlin 2.0+

    // Plugin de servicios de Google
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fitmind"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fitmind"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // 🔹 Firebase BoM (maneja versiones automáticamente)
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))

    // 🔸 SDKs de Firebase que usarás
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // 🔹 Librerías Compose y AndroidX estándar
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.8.0")
    
    // 🔹 Compose BOM para versiones consistentes
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    
    // 🔹 Navigation
    implementation("androidx.navigation:navigation-compose:2.8.3")
    
    // 🔹 DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // 🔹 WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // 🔹 Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
