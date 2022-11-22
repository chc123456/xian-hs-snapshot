import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.5"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "org.oldfather"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	maven {
		setUrl("https://maven.aliyun.com/repository/public/")
	}
	maven {
		setUrl("https://maven.aliyun.com/repository/spring/")
	}
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("io.github.bonigarcia:webdrivermanager:5.0.3")
	implementation("org.seleniumhq.selenium:selenium-java:3.141.59")
	implementation("org.seleniumhq.selenium:selenium-api:3.141.59")
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.2")
	implementation("ru.yandex.qatools.ashot:ashot:1.5.4")


	testImplementation("org.springframework.boot:spring-boot-starter-test")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
