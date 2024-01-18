plugins {
    `java-library`
    `maven-publish`
    signing
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.jfxcore"
version = project.findProperty("TAG_VERSION_PROJECT") ?: "1.0-SNAPSHOT"

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

javafx {
    version = project.ext["javafx.version"] as String
    modules = listOf("javafx.base", "javafx.graphics", "javafx.controls")
    configurations = arrayOf("compileOnly", "testImplementation")
}

dependencies {
    api("org.openjfx:javafx-base:${project.ext["javafx.version"]}")
    api("org.openjfx:javafx-graphics:${project.ext["javafx.version"]}")

    testImplementation("org.testfx:testfx-core:4.0.17")
    testImplementation("org.testfx:testfx-junit5:4.0.17")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

tasks.withType(Javadoc::class) {
    exclude("impl") // exclude implementation details

    (options as CoreJavadocOptions).apply {
        val sourceSetDirectories = sourceSets.main.get().java.sourceDirectories.joinToString(":")
        addStringOption("-source-path", sourceSetDirectories)
        addStringOption("Xmaxwarns").setValue("1000")
        addStringOption("Xmaxerrs").setValue("1000")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.jar)
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            pom {
                url.set("https://github.com/jfxcore/validation")
                name.set("validation")
                description.set("Validation framework for JavaFX applications")
                licenses {
                    license {
                        name.set("GPL-2.0-only WITH Classpath-exception-2.0")
                        url.set("https://www.gnu.org/licenses/gpl-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("jfxcore")
                        name.set("JFXcore")
                        organization.set("JFXcore")
                        organizationUrl.set("https://github.com/jfxcore")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/jfxcore/validation.git")
                    developerConnection.set("scm:git:https://github.com/jfxcore/validation.git")
                    url.set("https://github.com/jfxcore/validation")
                }
            }
        }
    }
    repositories {
        maven {
            if (project.hasProperty("REPOSITORY_USERNAME")
                && project.hasProperty("REPOSITORY_PASSWORD")
                && project.hasProperty("REPOSITORY_URL")) {
                credentials {
                    username = project.property("REPOSITORY_USERNAME") as String
                    password = project.property("REPOSITORY_PASSWORD") as String
                }
                url = uri(project.property("REPOSITORY_URL") as String)
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}
