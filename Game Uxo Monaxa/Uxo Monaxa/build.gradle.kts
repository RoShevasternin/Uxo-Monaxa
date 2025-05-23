buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
//    dependencies {
//
//    }
}

plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false
    id("org.jetbrains.kotlin.jvm") version "2.0.0" apply false
    id("com.android.library") version "8.7.3" apply false
}