plugins {
    kotlin("jvm") version "2.0.0"
    antlr
}

group = "xyz.olix3001"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.5")

    testImplementation(kotlin("test"))
}

sourceSets {
    main {
        java {
            srcDir(tasks.generateGrammarSource)
        }
    }
}

tasks.generateGrammarSource {
    outputDirectory = file("${project.buildDir}/generated/sources/main/java/antlr")
    arguments = listOf("-visitor", "-long-messages", "-package", "xyz.olix3001.grammar")

    doLast {
        copy {
            from("${project.buildDir}/generated/sources/main/java/antlr")
            into("${project.buildDir}/generated/sources/main/java/antlr/xyz/olix3001/grammar")
            include("*.java")
        }
        delete(fileTree("${project.buildDir}/generated/sources/main/java/antlr").matching {
            include("*.java")
        })
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}