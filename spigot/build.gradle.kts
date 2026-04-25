plugins {
  id("java")
  kotlin("jvm") version "1.9.20"
  id("com.gradleup.shadow") version "9.2.2"
}

val jarName = "animatium-spigot"

group = "com.github.ieatglu3"
version = "1.0.0"

repositories {
  mavenCentral()
  maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
  maven { url = uri("https://repo.maven.apache.org/maven2/") }
  maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
  maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
}

dependencies {
  implementation(project(":api"))
  implementation(project(":payloadcrafter"))
  compileOnly("org.spigotmc:spigot-api:1.13-R0.1-SNAPSHOT")
  compileOnly("com.github.retrooper:packetevents-api:2.12.0")
  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {

  withType<JavaCompile> {
    options.encoding = "UTF-8"
  }

  shadowJar {
    archiveBaseName = jarName
    version = project.version
    archiveClassifier = "shaded"
  }

  jar {
    archiveBaseName = jarName
    version = project.version
    dependsOn(shadowJar)
  }

  test {
    useJUnitPlatform()
  }
}