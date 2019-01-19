rootProject.name = "rokusho"

include("application")

listOf("javafx-commons", "javafx-controls", "core", "presenter", "launcher").forEach {
    include(it)
    project(":$it").projectDir = file("${rootProject.projectDir}/modules/$it")
}
