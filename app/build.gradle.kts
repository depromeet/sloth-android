import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION", "PropertyName")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.service)
    alias(libs.plugins.hilt)
    alias(libs.plugins.secrets.gradle.plugin)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kapt)
}

android {
    namespace = "com.depromeet.sloth"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.depromeet.sloth"
        minSdk = 24
        targetSdk = 33
        versionCode = 35
        versionName = "1.1.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GOOGLE_BASE_URL", properties["GOOGLE_BASE_URL"] as String)
        buildConfigField(
            "String",
            "POLICY_WEB_VIEW_URL",
            properties["POLICY_WEB_VIEW_URL"] as String
        )
    }

    signingConfigs {
        create("release") {
            val propertiesFile = rootProject.file("keystore.properties")
            val properties = Properties()
            properties.load(propertiesFile.inputStream())
            storeFile = file(properties["STORE_FILE"] as String)
            storePassword = properties["STORE_PASSWORD"] as String
            keyAlias = properties["KEY_ALIAS"] as String
            keyPassword = properties["KEY_PASSWORD"] as String
        }
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "SLOTH_BASE_URL", properties["DEBUG_BASE_URL"] as String)
        }

        getByName("release") {
            isDebuggable = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "SLOTH_BASE_URL", properties["RELEASE_BASE_URL"] as String)
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":presentation"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.startup)
    implementation(libs.kakao.auth)
    implementation(libs.timber)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    coreLibraryDesugaring(libs.desugar.jdk)
}

