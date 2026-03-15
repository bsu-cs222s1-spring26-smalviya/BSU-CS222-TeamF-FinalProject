plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(group = "org.slf4j", name = "slf4j-nop", version = "2.0.9")
    implementation(group = "com.jayway.jsonpath", name = "json-path", version = "2.8.0")
}

tasks.test {
    useJUnitPlatform()
}