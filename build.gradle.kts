plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.wiseplanner"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(group = "org.slf4j", name = "slf4j-nop", version = "2.0.9")
    implementation("com.google.code.gson:gson:2.13.2")
}

tasks.test {
    useJUnitPlatform()
}

javafx {
    version = "25.0.1"
    modules("javafx.controls", "javafx.fxml","javafx.web")
}

application {
    mainClass.set("Main")
}

tasks.withType<JavaExec> {
    standardInput = System.`in`
}