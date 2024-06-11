buildscript {
    repositories {
        maven {
            setUrl("https://maven.localazy.com/repository/release/")
        }
    }
    dependencies {
        classpath(libs.localazy)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
}
