import org.gradle.internal.os.OperatingSystem

plugins {
    id("com.github.ben-manes.versions") version "0.20.0"
    id("org.jetbrains.kotlin.jvm") version "1.3.11" apply false
}


val submoduleProjects =
    listOf("launcher", "javafx-commons", "javafx-controls", "core", "presenter", "util")
        .map { project(":$it") }

val javafxClassifier = when(OperatingSystem.current()) {
    OperatingSystem.WINDOWS -> "win"
    OperatingSystem.MAC_OS -> "mac"
    OperatingSystem.LINUX -> "linux"
    else -> throw RuntimeException("Mavenレポジトリ上で対応するJavaFXの実装が存在しないOS")
}

allprojects {
    group = "com.github.am4dr.rokusho"
    version = "0.2.0-SNAPSHOT"
    repositories {
        mavenLocal()
        jcenter()
    }


    apply(plugin="org.jetbrains.kotlin.jvm")
    apply(plugin="java-library")

    configure<BasePluginConvention> {
        archivesBaseName = "rokusho-$name"
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        "api"(kotlin("stdlib-jdk8"))
        "implementation"(kotlin("reflect"))
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.0")

        "testImplementation"("org.junit.jupiter:junit-jupiter-params:5.2.0")
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.2.0")
        "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:5.2.0")

        "api"("org.slf4j:slf4j-api:1.7.25")
        "runtimeOnly"("ch.qos.logback:logback-classic:1.2.3")
    }
    tasks.withType(Test::class) {
        useJUnitPlatform()
    }
    tasks.withType(KotlinCompile::class) {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

project(":core") {
    dependencies {
        "api"(project(":util"))
        "implementation"("org.yaml:snakeyaml:1.21")
    }
}
project(":javafx-controls") {
    configure<JavaPluginConvention> {
        sourceSets {
            register("sample")
        }
    }
    val java = the<JavaPluginConvention>()
    dependencies {
        "implementation"(project(":javafx-commons"))
        "api"("org.openjfx:javafx-base:11.0.1:$javafxClassifier")
        "api"("org.openjfx:javafx-controls:11.0.1:$javafxClassifier")
        "api"("org.openjfx:javafx-graphics:11.0.1:$javafxClassifier")

        "sampleImplementation"(kotlin("reflect"))
        "sampleImplementation"("com.github.am4dr.javafx:gui-sample-viewer:0.4.2-SNAPSHOT")
        "sampleImplementation"(java.sourceSets["main"].runtimeClasspath)
    }
    tasks {
        register("runGuiSamples", JavaExec::class.java) {
            dependsOn("sampleClasses")
            doFirst {
                classpath(java.sourceSets["sample"].runtimeClasspath)
                main = "com.github.am4dr.rokusho.javafx.control.sample.SampleLauncher"
                args = listOf(
                    "--path=build/classes/kotlin/main",
                    "--load-only-path=build/classes/kotlin/sample")
            }
        }
        register("runGuiSamplesAllReload", JavaExec::class.java) {
            dependsOn("sampleClasses")
            doFirst {
                classpath(java.sourceSets["sample"].runtimeClasspath)
                main = "com.github.am4dr.rokusho.javafx.control.sample.SampleLauncher"
                val path = listOf("build/classes/kotlin/main", "build/classes/kotlin/sample").joinToString(File.pathSeparator)
                args = listOf("--path=$path")
            }
        }
    }
}
project(":presenter") {
    dependencies {
        "api"(project(":core"))
        "implementation"(project(":javafx-controls"))
        "implementation"(project(":javafx-commons"))
    }
}
project(":launcher") {
    dependencies {
        "implementation"(project(":presenter"))
        "implementation"(project(":javafx-controls"))
        "implementation"(project(":javafx-commons"))
        "implementation"("commons-cli:commons-cli:1.4")
    }
}
project(":javafx-commons") {
    dependencies {
        "api"("org.openjfx:javafx-base:11.0.1:$javafxClassifier")
    }
}

project(":application") {
    apply(plugin="application")

    configure<ApplicationPluginConvention> {
        mainClassName = "com.github.am4dr.rokusho.launcher.GUILauncher"
    }

    dependencies {
        submoduleProjects.forEach {
            "runtimeOnly"(it)
        }
    }
    tasks {
        named<Jar>("jar") {
            onlyIf {
                false
            }
        }
        named<JavaExec>("run") {
            doFirst {
                if (project.hasProperty("args")) {
                    val projectArgs: String by project
                    args = projectArgs.split("\\s+")
                }
            }
        }
    }
}
