import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61" 
}


group = "org.chepelov.sgcib.kata"
version = "0.0-SNAPSHOT"


val koin_version = "2.0.1"
val kotlintest_version = "3.4.2"

repositories {
    jcenter() 
}

dependencies {
    implementation(kotlin("stdlib"))    
    implementation(kotlin("stdlib-jdk8"))
    // implementation(kotlin("reflect"))
    testImplementation("io.kotlintest:kotlintest-runner-junit5:$kotlintest_version")
    testImplementation("org.slf4j:slf4j-simple:1.7.26")

    implementation("org.koin:koin-core:$koin_version")
    testImplementation("org.koin:koin-test:$koin_version")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

