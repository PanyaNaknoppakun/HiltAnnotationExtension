plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(kotlin("stdlib"))
}

publishing {
    publications {
        publishing {
            publications {
                create<MavenPublication>("release") {
                    from(components["java"])
                    groupId = "com.pls"
                    artifactId = "annotation"
                    version = "1.0.0"
                }
            }
        }
    }
}
