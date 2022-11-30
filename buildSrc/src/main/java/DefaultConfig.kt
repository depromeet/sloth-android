import org.gradle.api.JavaVersion

object DefaultConfig {
    const val NAMESPACE = "com.depromeet.sloth"
    const val APPLICATION_ID = "com.depromeet.sloth"
    const val COMPILE_SDK_VERSION = 32
    const val MIN_SDK_VERSION = 24
    const val TARGET_SDK_VERSION = 32
    const val VERSION_CODE = 20
    const val VERSION_NAME = "1.0.15"
    val javaCompileTarget = JavaVersion.VERSION_11
}