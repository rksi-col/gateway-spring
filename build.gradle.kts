import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    id("org.springframework.boot") version "3.4.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.5"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

// ЕДИНАЯ ВЕРСИЯ ДЛЯ ВСЕГО
val protobufVersion = "3.25.5"
val grpcVersion = "1.68.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("net.devh:grpc-client-spring-boot-starter:3.0.0.RELEASE")

    implementation("com.google.protobuf:protobuf-java:$protobufVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")

    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-services:$grpcVersion")

    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("com.itextpdf:itext7-core:7.2.5")
    implementation("com.itextpdf:font-asian:7.2.5")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        all().configureEach {
            plugins {
                create("grpc")
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

//import com.google.protobuf.gradle.*

//plugins {
//    kotlin("jvm") version "2.1.20"
//    kotlin("plugin.spring") version "2.1.20"
//    id("org.springframework.boot") version "3.4.7"
//    id("io.spring.dependency-management") version "1.1.7"
//    id("com.google.protobuf") version "0.9.5"
//
//}
//
//group = "com.example"
//version = "0.0.1-SNAPSHOT"
//
//java {
//    toolchain {
//        languageVersion = JavaLanguageVersion.of(17)
//    }
//}
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//    // Spring Boot Web
//    implementation("org.springframework.boot:spring-boot-starter-web")
//
//    // gRPC Client (проверенный стартер)
//    implementation("net.devh:grpc-client-spring-boot-starter:3.1.0.RELEASE")
//
//    // Protobuf — СТРОГО ОДНА ВЕРСИЯ!
//    implementation("com.google.protobuf:protobuf-java:3.25.8")
//    implementation("com.google.protobuf:protobuf-kotlin:3.25.8")
//
//    // gRPC — совместимые версии
//    implementation("io.grpc:grpc-stub:1.68.2")
//    implementation("io.grpc:grpc-protobuf:1.68.2")
//    implementation("io.grpc:grpc-netty-shaded:1.68.2")
//    implementation("io.grpc:grpc-services:1.68.2")
//
//    // Аннотации
//    implementation("javax.annotation:javax.annotation-api:1.3.2")
//
//    // Kotlin
//    implementation("org.jetbrains.kotlin:kotlin-reflect")
//    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
//
//    // Тесты
//    testImplementation("org.springframework.boot:spring-boot-starter-test")
//}
//
//protobuf {
//    protoc {
//        artifact = "com.google.protobuf:protoc:3.25.5"
//    }
//    plugins {
//        create("grpc") {
//            artifact = "io.grpc:protoc-gen-grpc-java:1.68.1"
//        }
//    }
//    generateProtoTasks {
//        all().configureEach {
//            plugins {
//                create("grpc")  // ← Должен быть вызван!
//            }
//        }
//    }
//}
//
//tasks.withType<Test> {
//    useJUnitPlatform()
//}
//
//
//sourceSets {
//    main {
//        kotlin {
//            srcDir("src/main/java")  // ← Важно!
//        }
//    }
//}