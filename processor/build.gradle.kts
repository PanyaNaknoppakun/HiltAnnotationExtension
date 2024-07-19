plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("org.jetbrains.kotlin.kapt")
    `maven-publish`
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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "com.pls"
            artifactId = "auto-hilt"
            version = "1.0.0"
        }
    }
    repositories {
        maven {
            url = uri("https://your-repository-url")
        }
    }
}
