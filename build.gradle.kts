import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.diffplug.spotless") version "6.25.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.gradleup.shadow") version "8.3.3"
}

group = "pl.kodstark"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    testing {
        imports {
            mavenBom("org.junit:junit-bom:5.10.0")
        }
        dependencies {
            dependency("org.mockito:mockito-core:5.14.2")
        }
    }
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation("net.bytebuddy:byte-buddy:1.15.4")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("io.reactivex.rxjava3:rxjava:3.1.9")
    mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}

tasks.withType<ShadowJar> {
    manifest {
        attributes("Premain-Class" to "pl.kodstark.rxjava.agent.SubscriptionAgent")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs = listOf(
        "-javaagent:${tasks.shadowJar.get().archiveFile.get().asFile}", "-javaagent:${mockitoAgent.asPath}"
    )
}

tasks.test {
    dependsOn("shadowJar")
}

spotless {
    java {
        removeUnusedImports()
        googleJavaFormat().skipJavadocFormatting()
    }
}