plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("org.jetbrains.kotlin.kapt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


dependencies {

    implementation(libs.kotlin.stdlib)
    kapt(libs.auto.service)
    compileOnly(libs.auto.service.annotations)
    // Add javax.annotation.processing API
    implementation(project(":annotation"))
    implementation(libs.kotlinpoet)
    implementation(libs.javapoet)
    implementation(libs.dagger)

}

