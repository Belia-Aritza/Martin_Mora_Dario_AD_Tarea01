plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.iesalandalus.programacion.tallermecanico"
version = "1.0"

repositories {
    mavenCentral()
    // Repositorio JitPack: necesario para la librería "entrada".
    maven { url = uri("https://jitpack.io") }
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    // JUnit Jupiter
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // Mockito
    testImplementation("org.mockito:mockito-core:+")
    // Entrada
    implementation("com.github.JRJimenezReyes:entrada:1.0.3")
    // Jackson (JSON y XML)
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.18.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
    // Los tests esperan el formato numérico español (por ejemplo, "10,00 €"): se fuerza el idioma
    // para que no dependa de la configuración regional de la máquina o del daemon de Gradle.
    systemProperty("user.language", "es")
    systemProperty("user.country", "ES")
}

application {
    mainClass.set("org.iesalandalus.programacion.tallermecanico.Main")
}

tasks.run.configure {
    standardInput = System.`in`
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "org.iesalandalus.programacion.tallermecanico.Main"
    }
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}
