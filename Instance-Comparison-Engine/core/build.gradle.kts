plugins {
    id("application")
    id("io.freefair.lombok") version "8.6"
}

version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("../libs/speedy-0.2.jar"))
    implementation("commons-io:commons-io:2.15.1")
    implementation("com.google.guava:guava:33.1.0-jre")
    implementation("org.apache.commons:commons-csv:1.10.0")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.jgrapht:jgrapht-core:0.9.2")
    implementation("org.tinyjee.jgraphx:jgraphx:3.4.1.3")
    implementation("org.jgrapht:jgrapht-core:0.9.2")
    implementation("org.jgrapht:jgrapht-ext:0.9.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")
    
    implementation("org.slf4j:slf4j-api:2.0.12")
    runtimeOnly("ch.qos.logback:logback-core:1.5.1")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.1")

    implementation("org.jdom:jdom:1.1")

    testImplementation("junit:junit:4.13.2")
}

application {
    mainClass = "ic.Main" 
}



tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
    }
    withType<JavaExec> { 
        jvmArgs = listOf("-Xms2048m", "-Xmx"+project.properties["java.maxmemory"].toString())
    }
}