import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61" 
}


group = "org.chepelov.sgcib.kata"
version = "0.0-SNAPSHOT"

repositories {
    jcenter() 
}

dependencies {
    implementation(kotlin("stdlib"))    
    implementation(kotlin("stdlib-jdk8"))
    // implementation(kotlin("reflect"))
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
    testImplementation("org.slf4j:slf4j-simple:1.7.26")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

