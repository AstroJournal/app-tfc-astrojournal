# AstroJournal

Proyecto Android en Kotlin con Jetpack Compose + MVVM.

## Requisitos

- Android Studio reciente (Koala o superior recomendado)
- JDK 17 (Android Studio ya incluye JBR 17)
- Android SDK instalado (segun `compileSdk` del proyecto)
- Emulador o dispositivo Android para tests instrumentados
- Conexion a internet para APIs (APOD / visibilidad)

## Como ejecutar

Desde la raiz del proyecto:

- Compilar app debug:
  - `./gradlew :app:assembleDebug`
- Unit tests:
  - `./gradlew :app:testDebugUnitTest`
- Compilar tests instrumentados:
  - `./gradlew :app:compileDebugAndroidTestKotlin`
- Ejecutar tests instrumentados (con emulador encendido):
  - `./gradlew :app:connectedDebugAndroidTest`

En Windows tambien puedes usar `gradlew.bat` en lugar de `./gradlew`.

## Notas

- APOD usa `DEMO_KEY` de NASA, puede tener limite de peticiones.
- Si hay problemas de sync en otra maquina, abrir con Android Studio y hacer `Sync Project with Gradle Files`.
