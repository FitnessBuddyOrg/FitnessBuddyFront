plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.project.fitnessbuddy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.project.fitnessbuddy"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src\\main\\assets", "src\\main\\assets")
            }
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
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
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.hilt.lifecycle.viewmodel)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    implementation(libs.okhttp)

    implementation(libs.androidx.room.ktx)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.youtubePlayer)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.process)

    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)


    testRuntimeOnly(libs.junit.jupiter.engine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.play.services.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.googleid)
    implementation(libs.androidx.credentials.play.services.auth)

    implementation(libs.mpandroidchart)

    implementation(libs.coil.compose)

    implementation (libs.ucrop)

    testImplementation(kotlin("test"))


    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.androidx.room.compiler)
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs = listOf("-Dnet.bytebuddy.experimental=true", "-XX:+EnableDynamicAgentLoading")

    reports {
        html.required.set(true)
        html.outputLocation.set(file("${layout.buildDirectory}/reports/tests"))
    }
}