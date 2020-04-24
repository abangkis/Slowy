plugins {
    id("org.jetbrains.kotlin.js") version "1.3.72"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.3.72"
}

group = "net.mreunionlabs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.3")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.72") // or "kotlin-stdlib-jdk8"
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0")

//    implementation(npm("left-pad", "1.3.0"))

    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
}

//kotlin.target.browser { }

kotlin {
    target {
        // You can drop browser or node if you targeting only one
        browser()
//        nodejs()
    }
    sourceSets["main"].dependencies {
        implementation(npm("is-sorted"))
    }
//
////    sourceSets["main"].dependencies {
////        implementation(npm("left-pad", "1.3.0"))
////    }
}