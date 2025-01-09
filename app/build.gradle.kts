import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.blackbeard"
    compileSdk = 35

    defaultConfig {
        // Load the local.properties file
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        // Set the TMDB_API key in BuildConfig
        buildConfigField("String", "TMDB_API", "\"${properties["TMDB_API"]}\"")

        applicationId = "com.example.blackbeard"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            buildConfigField("String", "TMDB_API", "\"${properties["TMDB_API"]}\"")
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
        buildConfig = true
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.materialIconsExtended)
    implementation(libs.accompanistPager)
    implementation(libs.lifecycleViewModelCompose)
    implementation(libs.coreKtx)
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.activityCompose)
    implementation(libs.datastorePreferences)

    implementation(platform(libs.composeBom))
    implementation(libs.composeUi)
    implementation(libs.composeUiGraphics)
    implementation(libs.composeUiToolingPreview)
    implementation(libs.material3)
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseAnalytics)
    implementation(libs.firebaseFirestoreKtx)

    implementation(libs.kotlinxSerializationJson)
    implementation(libs.retrofitConverterKotlinxSerialization)
    implementation(libs.navigationCompose)
    implementation(libs.navigationRuntimeKtx)
    implementation(libs.retrofit)
    implementation(libs.coilCompose)
    implementation(libs.coilNetworkOkhttp)
    implementation(libs.firebaseInstallations)

    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.mockitoKotlin)
    androidTestImplementation(libs.mockitoAndroid)
    testImplementation(libs.kotlinxCoroutinesTest)

    debugImplementation(libs.composeUiTooling)
    debugImplementation(libs.composeUiTestManifest)
}