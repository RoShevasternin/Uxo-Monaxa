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
    id("com.android.application") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20" apply false

    id("org.jetbrains.kotlin.jvm") version "2.1.20" apply false
    id("com.android.library") version "8.7.3" apply false
}