plugins {
    id("java")
    id("xyz.jpenilla.run-velocity") version "2.3.1"
}

group = "de.joker"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
}

tasks {
    runVelocity {
        velocityVersion("3.4.0-SNAPSHOT")
    }

    processResources {
        filesMatching("velocity-plugin.json") {
            expand(
                "version" to project.version,
                "name" to project.name
            )
        }
    }

    compileJava {
        options.release.set(21)
    }
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}