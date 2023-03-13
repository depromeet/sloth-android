import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION", "PropertyName")
plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    id(libs.plugins.google.service.get().pluginId)
    id(libs.plugins.hilt.android.get().pluginId)
    id(libs.plugins.secrets.gradle.plugin.get().pluginId)
    id(libs.plugins.navigation.safeargs.get().pluginId)
    id(libs.plugins.firebase.crashlytics.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

android {
    namespace = "com.depromeet.sloth"
    compileSdk = ProjectConfig.compileSdk

    defaultConfig {
        applicationId = "com.depromeet.sloth"
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk
        versionCode = ProjectConfig.versionCode
        versionName = ProjectConfig.versionName

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
            isDebuggable = false
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
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(libs.core)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.splash)
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)

    implementation(libs.bundles.navigation)
    implementation(libs.bundles.lifecycle)
    implementation(libs.activity)
    implementation(libs.fragment)
    implementation(libs.recyclerview)
    implementation(libs.paging3)
    implementation(libs.datastore)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.fragment)
    implementation(libs.startup)

    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.okhttp)
    implementation(libs.bundles.coroutine)
    implementation(libs.glide)
    kapt(libs.glide.compiler)
    implementation(libs.google.auth)
    implementation(libs.kakao.auth)
    implementation(libs.progressview)
    implementation(libs.lottie)
    implementation(libs.timber)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)
}

