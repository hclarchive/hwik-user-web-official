import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.hwik.hcl"
version = "0.9.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    implementation("org.web3j:core:4.10.0")
    implementation("org.web3j:contracts:4.10.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")

    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    runtimeOnly("com.mysql:mysql-connector-j")
}
