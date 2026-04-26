plugins {
  id("java")
  kotlin("jvm") version "1.9.20"
  id("com.gradleup.shadow") version "9.2.2"
  id("maven-publish")
}

val jarName = "animatium-spigot"

group = "com.github.ieatglu3"
version = "1.0.0"

repositories {
  mavenCentral()
  maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
  maven("https://repo.maven.apache.org/maven2/")
  maven("https://repo.codemc.io/repository/maven-releases/")
  maven("https://repo.codemc.io/repository/maven-snapshots/")
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

java {
  withSourcesJar()
  withJavadocJar()
}

tasks {

  withType<JavaCompile> {
    options.encoding = "UTF-8"
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

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])
      groupId = project.group.toString()
      artifactId = jarName
      version = project.version.toString()
    }
  }
}