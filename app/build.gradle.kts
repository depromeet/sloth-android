import java.util.Properties

plugins {
    id(Plugins.ANDROID_APPLICATION)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KAPT)
    id(Plugins.PARCELIZE)
    id(Plugins.GOOGLE_SERVICE)
    id(Plugins.HILT_PLUGIN)
    id(Plugins.SECRETS_GRADLE_PLUGIN)
    id(Plugins.SAFEARGS)
}

android {
    namespace = DefaultConfig.NAMESPACE
    compileSdk = DefaultConfig.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = DefaultConfig.APPLICATION_ID
        minSdk = DefaultConfig.MIN_SDK_VERSION
        targetSdk = DefaultConfig.TARGET_SDK_VERSION
        versionCode = DefaultConfig.VERSION_CODE
        versionName = DefaultConfig.VERSION_NAME

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
            storeFile = File(propertiesFile.parentFile, "sloth.jks")
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
        jvmTarget = DefaultConfig.javaCompileTarget.toString()
    }

    buildFeatures {
        dataBinding = true
    }

    dependencies {
        implementation(Dependencies.CORE_KTX)
        implementation(Dependencies.APP_COMPAT)
        implementation(Dependencies.MATERIAL)
        implementation(Dependencies.CONSTRAINT_LAYOUT)

        testImplementation(Testings.JUNIT4)
        androidTestImplementation(Testings.ANDROID_JUNIT)
        androidTestImplementation(Testings.ESPRESSO_CORE)

        // Navigation
        implementation(Dependencies.NAVIGATION_FRAGMENT_KTX)
        implementation(Dependencies.NAVIGATION_UI_KTX)

        // Retrofit2
        implementation(Dependencies.RETROFIT)
        implementation(Dependencies.RETROFIT_CONVERTER_GSON)
        implementation(Dependencies.RETROFIT_CONVERTER_SCALARS)

        // Okhttp3
        implementation(Dependencies.OKHTTP)
        implementation(Dependencies.OKHTTP_LOGGING_INTERCEPTOR)

        // Coroutine
        implementation(Dependencies.COROUTINE_CORE)
        implementation(Dependencies.COROUTINE_ANDROID)

        // ViewModel
        implementation(Dependencies.LIFECYCLE_VIEWMODEL_KTX)
        implementation(Dependencies.LIFECYCLE_RUNTIME_KTX)
        implementation(Dependencies.LIFECYCLE_SAVEDSTATE)

        // ViewModel delegate
        implementation(Dependencies.ACTIVITY_KTX)
        implementation(Dependencies.FRAGMENT_KTX)

        // ConcatAdapter
        implementation(Dependencies.RECYCLERVIEW)

        // DataStore
        implementation(Dependencies.DATASTORE)

        // Hilt
        implementation(Dependencies.DAGGER_HILT)
        kapt(Dependencies.DAGGER_HILT_KAPT)

        // Glide
        implementation(Dependencies.GLIDE)
        kapt(Dependencies.GLIDE_COMPILER)

        // Kakao login
        implementation(Dependencies.KAKAO_AUTH)

        // Google login
        implementation(Dependencies.GOOGLE_AUTH)

        // ProgressView
        implementation(Dependencies.PROGRESSVIEW)

        // Firebase
        implementation(platform(Dependencies.FIREBASE_BOM))
        implementation(Dependencies.FIREBASE_ANALYTICS)
        implementation(Dependencies.FIREBASE_MESSAGING)

        // Lottie
        implementation(Dependencies.LOTTIE)

        // Timber
        implementation(Dependencies.TIMBER)
    }
}

