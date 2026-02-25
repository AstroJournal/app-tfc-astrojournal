plugins {
    // Plugins principales del módulo compartido (KMP + Android + Lint + SQLDelight)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    id("app.cash.sqldelight") version "2.0.2"
}

kotlin {

    androidLibrary {
        compileSdk = 34
        namespace = "com.astrojournal.shared"
    }
    // Nombre base para los frameworks de iOS
    val xcfName = "sharedKit"

    // Targets iOS (generan frameworks para Xcode)
    iosX64 {
        binaries.framework { baseName = xcfName }
    }
    iosArm64 {
        binaries.framework { baseName = xcfName }
    }
    iosSimulatorArm64 {
        binaries.framework { baseName = xcfName }
    }

    sourceSets {

        // Código común para todas las plataformas
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib)

                // Corrutinas
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

                // Ktor (cliente HTTP multiplataforma)
                implementation("io.ktor:ktor-client-core:2.3.4")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.4")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")

                // Serialización JSON
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

                // SQLDelight runtime común
                implementation("app.cash.sqldelight:runtime:2.0.2")
                implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
            }
        }

        // Tests comunes
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        // Código específico de Android
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:2.3.4")

                // Driver SQLDelight para Android
                implementation("app.cash.sqldelight:android-driver:2.0.2")
            }
        }

        // Tests en dispositivo Android (por si el equipo los usa)
        maybeCreate("androidDeviceTest").dependencies {
            implementation(libs.androidx.runner)
            implementation(libs.androidx.core)
            implementation(libs.androidx.junit)
        }

        // Código específico de iOS
        maybeCreate("iosMain").dependencies {
            implementation("io.ktor:ktor-client-darwin:2.3.4")
            implementation("app.cash.sqldelight:native-driver:2.0.2")
        }
    }
}

// Configuración de SQLDelight (genera la base de datos y las queries)
sqldelight {
    databases {
        create("AstrojournalDatabase") {
            packageName.set("com.astrojournal.shared.data.db")
            verifyMigrations.set(false)
        }
    }
}
tasks.matching { it.name.contains("verify", ignoreCase = true) }.configureEach {
    enabled = false
}

