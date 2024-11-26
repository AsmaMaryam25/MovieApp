import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.example.movieapp"
    compileSdk = 35

    defaultConfig {
        // Load the local.properties file
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        // Set the TMDB_API key in BuildConfig
        buildConfigField("String", "TMDB_API", "\"${properties["TMDB_API"]}\"")

        applicationId = "com.example.movieapp"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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

        implementation(libs.kotlinxSerializationJson)
        implementation(libs.retrofitConverterKotlinxSerialization)
        implementation(libs.navigationCompose)
        implementation(libs.navigationRuntimeKtx)
        implementation(libs.retrofit)
        implementation(libs.coilCompose)
        implementation(libs.coilNetworkOkhttp)

        debugImplementation(libs.composeUiTooling)
        debugImplementation(libs.composeUiTestManifest)
    }
}