# Correcciones y Ampliación para la Memoria del TFC: AstroJournal

A continuación, se presenta la redacción ampliada y detallada con ejemplos de código extraídos directamente del proyecto (ViewModels, Repositories y esquemas SQL) para cubrir todas las correcciones y ampliaciones exigidas en la memoria del TFC.

---

## 1. Arquitectura y Persistencia de Datos (SQLDelight)
*Para la sección de diseño del Backend o Gestión de Datos.*

### 1.1. Descripción del Backend Local y SQLDelight

Para la gestión de datos en **AstroJournal** hemos optado por prescindir inicialmente de un servidor remoto tradicional, centralizando toda la lógica de negocio y persistencia en un módulo compartido construido con **Kotlin Multiplatform (KMP)**. La persistencia de datos se gestiona a través de **SQLDelight**, una librería multiplataforma *SQL-first* que genera código Kotlin con seguridad de tipos en tiempo de compilación (*type-safe APIs*) a partir de sentencias SQL puras escritas en ficheros `.sq`.

La principal ventaja de SQLDelight radica en que **cualquier error en las consultas SQL queda detectado en tiempo de compilación**, no en ejecución, eliminando una amplia categoría de errores en tiempo de producción. El código generado automáticamente crea interfaces de consulta (`*Queries`) que son inyectadas en los `Repository` correspondientes.

La arquitectura de datos sigue el patrón **Repository**, actuando como única fuente de verdad para la UI. El flujo completo es el siguiente:

```
UI (Jetpack Compose) ← StateFlow ← ViewModel ← Repository ← SQLDelight Queries ← SQLite
```

El flujo de datos es totalmente **reactivo y asíncrono**: los `ViewModel` utilizan `viewModelScope.launch` para lanzar corrutinas en el dispatcher `Dispatchers.IO`, evitando bloquear el hilo principal de Android. Los resultados se publican como `StateFlow`, que la UI observa de forma declarativa con `collectAsState()` en Jetpack Compose.

### 1.2. Modelo de Datos y Esquema Relacional Completo

El esquema de datos local (`AstrojournalDatabase`) se sostiene sobre tres tablas principales:

#### Tabla `user` — Gestión de Autenticación Local

```sql
-- User.sq
CREATE TABLE user (
    id        INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    username  TEXT    NOT NULL UNIQUE,
    email     TEXT    NOT NULL UNIQUE,
    password  TEXT    NOT NULL,
    createdAt TEXT    NOT NULL
);
```

- **`username`** y **`email`** tienen restricciones `UNIQUE` a nivel de esquema, impidiendo registros duplicados directamente desde la base de datos.
- La **contraseña nunca se almacena en texto plano**: antes de persistirla, el `RegisterViewModel` aplica el algoritmo `SHA-256` para producir un *hash* irreversible.
- El campo `createdAt` registra el timestamp ISO-8601 del momento de alta del usuario.

#### Tabla `MeetupEvent` — Red Social / Quedadas Astronómicas

```sql
-- MeetupEvent.sq
CREATE TABLE MeetupEvent (
    id                  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title               TEXT    NOT NULL,
    description         TEXT    NOT NULL,
    location            TEXT    NOT NULL,
    dateTime            TEXT    NOT NULL,
    isMine              INTEGER NOT NULL DEFAULT 0,
    linkedAstroEventName TEXT
);
```

- El campo `isMine` actúa como un **flag booleano** (0 = evento comunitario, 1 = evento del usuario autenticado). SQLite no dispone de tipo booleano nativo, por lo que se emplea un `INTEGER` siguiendo la convención del motor. En Kotlin, la conversión bidireccional se gestiona en el `Repository`.
- **`linkedAstroEventName`** es una columna opcional (`NULL`able) que implementa la **relación lógica** entre una quedada social y un evento astronómico real calculado por la algoritmia local. Permite filtrar y mostrar en la UI qué fenómeno celeste motivó la quedada.

#### Tabla `collectible` — Cuaderno de Bitácora Personal

```sql
-- Collectible.sq
CREATE TABLE collectible (
    id              INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    eventId         INTEGER NOT NULL,
    eventName       TEXT    NOT NULL DEFAULT '',
    observationDate TEXT    NOT NULL,
    notes           TEXT,
    observed        INTEGER NOT NULL DEFAULT 0,
    agended         INTEGER NOT NULL DEFAULT 0
);
```

- **`eventId`** referencia el identificador numérico del evento astronómico (calculado localmente), estableciendo la relación entre el scrapbook personal y el calendario de eventos.
- **`notes`** es `NULL`able: solo se persiste cuando el usuario escribe algún texto, optimizando el espacio en disco.
- Los campos `observed` y `agended` son flags de estado que permiten al usuario marcar un evento como *"observado"* o *"agendado"*.

### 1.3. Consultas SQL y Operaciones CRUD Completas

SQLDelight exige que todas las operaciones sobre la base de datos estén definidas como consultas nombradas en los ficheros `.sq`. Esto garantiza que cada operación sea verificada en compilación.

**Consultas de `MeetupEvent.sq`:**

```sql
selectAll:
SELECT * FROM MeetupEvent;

selectMyEvents:
SELECT * FROM MeetupEvent WHERE isMine = 1;

insertEvent:
INSERT INTO MeetupEvent(title, description, location, dateTime, isMine, linkedAstroEventName)
VALUES (?, ?, ?, ?, ?, ?);

updateEvent:
UPDATE MeetupEvent
SET title = ?, description = ?, location = ?, dateTime = ?, linkedAstroEventName = ?
WHERE id = ? AND isMine = 1;

deleteById:
DELETE FROM MeetupEvent WHERE id = ? AND isMine = 1;
```

**Consultas de `User.sq`:**

```sql
selectByUsername:
SELECT * FROM user WHERE username = ?;

selectByEmail:
SELECT * FROM user WHERE email = ?;

selectByCredentials:
SELECT * FROM user WHERE email = ? AND password = ?;

insertUser:
INSERT INTO user(username, email, password, createdAt)
VALUES (?, ?, ?, ?);
```

**Consultas de `Collectible.sq`:**

```sql
selectByEventId:
SELECT * FROM collectible WHERE eventId = ?;

updateObserved:
UPDATE collectible
SET observed = :observed
WHERE id = :id;

updateNotes:
UPDATE collectible
SET notes = :notes
WHERE id = :id;

updateAgended:
UPDATE collectible
SET agended = :agended
WHERE id = :id;
```

---

## 2. Capa de Repositorios (Módulo Compartido KMP)

Los `Repository` actúan como la única puerta de acceso a la base de datos desde el resto de la aplicación. Encapsulan las `*Queries` generadas por SQLDelight y exponen una API Kotlin idiomática y limpia hacia los `ViewModel`.

### 2.1. `UserRepository.kt` — Autenticación y Unicidad

Un aspecto relevante del diseño es la gestión de la unicidad de usuarios. Aunque el esquema SQL ya tiene restricciones `UNIQUE`, el `UserRepository` verifica la unicidad **antes de insertar** mediante consultas previas de lectura. Esto permite devolver un resultado tipado (`sealed class UserInsertResult`) en lugar de capturar una excepción de base de datos genérica, ofreciendo mensajes de error específicos al usuario:

```kotlin
// shared/.../data/db/UserRepository.kt
sealed class UserInsertResult {
    object Success          : UserInsertResult()
    object DuplicateUsername: UserInsertResult()
    object DuplicateEmail   : UserInsertResult()
    data class Error(val message: String) : UserInsertResult()
}

class UserRepository(private val queries: UserQueries) {

    fun insertUser(
        username: String,
        email: String,
        passwordHash: String,
        createdAt: String
    ): UserInsertResult {
        // Comprobación previa de unicidad con mensajes específicos
        if (queries.selectByUsername(username).executeAsOneOrNull() != null)
            return UserInsertResult.DuplicateUsername
        if (queries.selectByEmail(email).executeAsOneOrNull() != null)
            return UserInsertResult.DuplicateEmail

        return try {
            queries.insertUser(username, email, passwordHash, createdAt)
            UserInsertResult.Success
        } catch (e: Exception) {
            UserInsertResult.Error(e.message ?: "Error desconocido")
        }
    }

    fun findByCredentials(email: String, passwordHash: String): User? =
        queries.selectByCredentials(email, passwordHash).executeAsOneOrNull()
}
```

### 2.2. `MeetupEventRepository.kt` — Eventos Sociales

```kotlin
// shared/.../data/db/MeetupEventRepository.kt
class MeetupEventRepository(private val queries: MeetupEventQueries) {

    fun insertEvent(
        title: String,
        description: String,
        location: String,
        dateTime: String,
        isMine: Boolean,
        linkedAstroEventName: String? = null
    ) {
        queries.insertEvent(
            title = title,
            description = description,
            location = location,
            dateTime = dateTime,
            isMine = if (isMine) 1L else 0L,   // Conversión Boolean → INTEGER
            linkedAstroEventName = linkedAstroEventName
        )
    }

    fun getAll(): List<MeetupEvent> = queries.selectAll().executeAsList()
    fun getMyEvents(): List<MeetupEvent> = queries.selectMyEvents().executeAsList()
    fun deleteById(id: Long) = queries.deleteById(id)
}
```

### 2.3. `CollectibleRepository.kt` — Cuaderno Personal

```kotlin
// shared/.../data/db/CollectibleRepository.kt
class CollectibleRepository(private val queries: CollectibleQueries) {

    fun insertCollectible(
        eventId: Long,
        eventName: String,
        observationDate: String,
        notes: String?,
        observed: Int,
        agended: Int
    ) {
        queries.insertCollectible(
            eventId          = eventId,
            eventName        = eventName,
            observationDate  = observationDate,
            notes            = notes,
            observed         = observed.toLong(),
            agended          = agended.toLong()
        )
    }

    fun updateObserved(id: Long, observed: Int) =
        queries.updateObserved(observed = observed.toLong(), id = id)

    fun updateNotes(id: Long, notes: String?) =
        queries.updateNotes(notes = notes, id = id)

    fun updateAgended(id: Long, agended: Int) =
        queries.updateAgended(agended = agended.toLong(), id = id)

    fun getAll(): List<Collectible> = queries.selectAll().executeAsList()
    fun getById(id: Long): Collectible? = queries.selectById(id).executeAsOneOrNull()
    fun deleteById(id: Long) = queries.deleteById(id)
}
```

---

## 3. Capa de Presentación: ViewModels y Gestión Reactiva del Estado

Todos los `ViewModel` de AstroJournal siguen el patrón **Unidirectional Data Flow (UDF)**: el estado fluye en una sola dirección desde el `ViewModel` hacia la UI mediante `StateFlow`. La UI nunca modifica el estado directamente, sino que lanza eventos/acciones al `ViewModel`.

### 3.1. `HomeViewModel.kt` — Cálculos Astronómicos Reactivos

El `HomeViewModel` es el más complejo en cuanto a lógica: orquesta el cálculo asíncrono de la fase lunar actual y la lista de próximos eventos astronómicos, ambos calculados **completamente en local** sin ninguna llamada a red:

```kotlin
// app/.../ui/viewmodels/HomeViewModel.kt

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: Astro) : UiState()
    data class Error(val message: String) : UiState()
}

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _upcomingEvents = MutableStateFlow<List<AstroEvent>>(emptyList())
    val upcomingEvents: StateFlow<List<AstroEvent>> = _upcomingEvents

    fun fetchMoonData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(500) // Retardo visual para mejorar la UX de carga

            try {
                // 1. Calcular fase lunar actual sin peticiones de red
                val moonPhaseInfo = MoonCalculator.getMoonPhaseInfo()
                val astroData = Astro(
                    moon_age          = moonPhaseInfo.moonAge,
                    moon_illumination = moonPhaseInfo.illumination.toString(),
                    moon_phase        = moonPhaseInfo.phaseName
                )

                // 2. Calcular próximos 7 eventos astronómicos
                fetchUpcomingEvents()

                _uiState.value = UiState.Success(astroData)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error: ${e.message ?: "Unknown error"}")
            }
        }
    }

    private fun fetchUpcomingEvents() {
        _upcomingEvents.value = AstroEventCalculator.getUpcomingEvents(limit = 7)
    }
}
```

El uso de `sealed class UiState` es un patrón clave: Jetpack Compose renderiza de forma diferente según si el estado es `Loading`, `Success` o `Error`, sin ningún flag booleano que pueda quedar en estado inconsistente.

### 3.2. `SocialEventsViewModel.kt` — CRUD con Estado Compuesto

El `SocialEventsViewModel` gestiona un estado más complejo mediante un `data class` que agrupa todas las propiedades de la pantalla, eliminando la necesidad de múltiples `StateFlow` independientes:

```kotlin
// app/.../ui/viewmodels/SocialEventsViewModel.kt

data class SocialEventsUiState(
    val allEvents        : List<MeetupEvent>  = emptyList(),
    val myEvents         : List<MeetupEvent>  = emptyList(),
    val upcomingAstroEvents: List<AstroEvent> = emptyList(),
    val searchQuery      : String             = "",
    val isLoading        : Boolean            = false,
    val error            : String?            = null
)

class SocialEventsViewModel(
    private val repository: MeetupEventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SocialEventsUiState())
    val uiState: StateFlow<SocialEventsUiState> = _uiState.asStateFlow()

    init { loadEvents() }

    fun loadEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val all   = repository.getAll()
                val my    = repository.getMyEvents()
                val astro = AstroEventCalculator.getUpcomingEvents(limit = 15)

                _uiState.update {
                    it.copy(allEvents = all, myEvents = my,
                            upcomingAstroEvents = astro, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun createEvent(
        title: String, description: String, location: String,
        dateTime: String, linkedAstroEventName: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertEvent(title, description, location, dateTime,
                                   isMine = true, linkedAstroEventName = linkedAstroEventName)
            loadEvents()
        }
    }

    fun deleteEvent(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteById(id)
            loadEvents()
        }
    }
}
```

Destacan dos decisiones de diseño relevantes: el uso de `Dispatchers.IO` para las operaciones de base de datos (que nunca deben ejecutarse en el hilo principal), y el uso de `_uiState.update { it.copy(...) }` para mutaciones atómicas del estado, evitando condiciones de carrera.

### 3.3. `EventDetailViewModel.kt` — Gestión de Concurrencia con Mutex

El `EventDetailViewModel` introduce un reto adicional de concurrencia: múltiples operaciones asíncronas (carga, inserción, actualización) pueden ejecutarse simultáneamente sobre el mismo registro de base de datos. La solución implementada utiliza un `Mutex` de Kotlinx Coroutines:

```kotlin
// app/.../ui/viewmodels/EventDetailViewModel.kt
class EventDetailViewModel(private val repo: CollectibleRepository) : ViewModel() {

    var collectibles    by mutableStateOf<List<Collectible>>(emptyList()); private set
    var isEventObserved by mutableStateOf(false); private set
    var isEventAgended  by mutableStateOf(false); private set
    var currentEventNote by mutableStateOf(""); private set

    private val dbMutex = Mutex() // Garantiza exclusión mutua en operaciones de BD

    init {
        loadCollectibles()
        cleanupDuplicates()
    }

    private suspend fun saveEventState(
        eventId: Long, eventName: String, date: String,
        note: String, agended: Boolean, observed: Boolean
    ) {
        dbMutex.withLock {                          // Solo una corrutina entra a la vez
            withContext(Dispatchers.IO) {
                val existing = repo.getAll().find { it.eventId == eventId }
                val shouldExist = agended || observed || note.isNotBlank()

                when {
                    shouldExist && existing != null -> {
                        repo.updateObserved(existing.id, if (observed) 1 else 0)
                        repo.updateAgended(existing.id, if (agended) 1 else 0)
                        repo.updateNotes(existing.id, note.ifBlank { null })
                    }
                    shouldExist -> {
                        repo.insertCollectible(eventId, eventName, date,
                            note.ifBlank { null },
                            if (observed) 1 else 0,
                            if (agended) 1 else 0)
                    }
                    existing != null -> repo.deleteById(existing.id) // Ya no necesario
                }
            }
        }
        loadCollectibles()
    }
}
```

### 3.4. `LoginViewModel.kt` / `RegisterViewModel.kt` — Autenticación con Hashing SHA-256

Ambos `ViewModel` implementan el mismo mecanismo de seguridad: antes de validar o persistir la contraseña, se calcula su *hash* SHA-256. Esto garantiza que la contraseña nunca se transmite ni almacena en texto plano:

```kotlin
// Función compartida en LoginViewModel y RegisterViewModel
private fun sha256(input: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
```

El `RegisterViewModel` también realiza **validación en cliente** antes de cualquier operación de base de datos, reduciendo las operaciones innecesarias:

```kotlin
// app/.../ui/viewmodels/RegisterViewModel.kt
fun register(username: String, email: String, password: String, repeatPassword: String) {
    if (username.isBlank()) {
        _uiState.value = RegisterUiState.Error("El nombre de usuario no puede estar vacío")
        return
    }
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        _uiState.value = RegisterUiState.Error("Formato de email inválido")
        return
    }
    if (password.length < 6) {
        _uiState.value = RegisterUiState.Error("La contraseña debe tener al menos 6 caracteres")
        return
    }
    if (password != repeatPassword) {
        _uiState.value = RegisterUiState.Error("Las contraseñas no coinciden")
        return
    }

    _uiState.value = RegisterUiState.Loading
    viewModelScope.launch(Dispatchers.IO) {
        val passwordHash = sha256(password)
        val createdAt    = LocalDateTime.now().toString()

        when (val result = userRepository.insertUser(username, email, passwordHash, createdAt)) {
            is UserInsertResult.Success          -> _uiState.value = RegisterUiState.Success
            is UserInsertResult.DuplicateUsername-> _uiState.value = RegisterUiState.Error("Este nombre de usuario ya está en uso")
            is UserInsertResult.DuplicateEmail   -> _uiState.value = RegisterUiState.Error("Este email ya está registrado")
            is UserInsertResult.Error            -> _uiState.value = RegisterUiState.Error(result.message)
        }
    }
}
```

---

## 4. Anexos Técnicos

### Anexo I: Estructura del Proyecto Multiplataforma (KMP)

El proyecto sigue la estructura estándar de Kotlin Multiplatform, separando claramente el código por plataforma:

```
app-tfc-astrojournal/
├── app/                                   ← Módulo Android
│   └── src/main/java/com/app/astrojournal/
│       ├── ui/
│       │   ├── screens/                   ← Composables (vistas declarativas)
│       │   │   ├── HomeScreen.kt
│       │   │   ├── Calendar.kt
│       │   │   ├── EventDetailScreen.kt
│       │   │   ├── SocialEventsScreen.kt
│       │   │   ├── LoginScreen.kt
│       │   │   └── RegisterScreen.kt
│       │   ├── viewmodels/                ← Lógica de presentación
│       │   │   ├── HomeViewModel.kt
│       │   │   ├── EventDetailViewModel.kt
│       │   │   ├── SocialEventsViewModel.kt
│       │   │   ├── LoginViewModel.kt
│       │   │   └── RegisterViewModel.kt
│       │   └── theme/                     ← Sistema de diseño (colores, formas, tipografía)
│       └── utils/
│           ├── MoonCalculator.kt          ← Cálculo fase lunar
│           └── AstroEventCalculator.kt    ← Motor eventos astronómicos
│
└── shared/                                ← Módulo KMP compartido
    └── src/commonMain/
        ├── kotlin/com/app/shared/data/db/
        │   ├── UserRepository.kt
        │   ├── MeetupEventRepository.kt
        │   ├── CollectibleRepository.kt
        │   └── Database.kt               ← Inicialización del driver SQLDelight
        └── sqldelight/com/astrojournal/shared/data/db/
            ├── User.sq
            ├── MeetupEvent.sq
            └── Collectible.sq
```

**Principio de separación de responsabilidades:**
- El módulo `shared` contiene **cero** dependencias de Android. Solo usa APIs Kotlin puras, lo que lo hace compilable también para iOS.
- El módulo `app` (Android) importa los `Repository` del módulo `shared` y los inyecta en los `ViewModel` a través de un `ViewModelProvider.Factory` customizado.

### Anexo II: Gestión del Ciclo de Vida y Corrutinas

El uso correcto de corrutinas en el contexto de Android es crítico para evitar fugas de memoria y bloqueos. En AstroJournal se aplican tres reglas fundamentales:

1. **`viewModelScope.launch`**: Todas las corrutinas de los `ViewModel` se lanzan en el scope del `ViewModel`, que se cancela automáticamente cuando el `ViewModel` es destruido. Esto previene fugas de memoria.

2. **`Dispatchers.IO` para I/O**: Las operaciones de base de datos siempre se ejecutan en el dispatcher `IO`, que usa un pool de hilos optimizados para operaciones bloqueantes.

3. **`withContext(Dispatchers.Main)` implícito**: Los `StateFlow` y `mutableStateOf` actualizados desde Coroutines se recogen automáticamente en el hilo principal por Jetpack Compose, sin necesidad de `postValue`.

```
                 ┌──────────────────────────────┐
                 │        Jetpack Compose        │
                 │  collectAsState(uiState)      │
                 └────────────┬─────────────────┘
                              │ (Main Thread)
                 ┌────────────▼─────────────────┐
                 │         ViewModel             │
                 │  viewModelScope.launch { }    │
                 └────────────┬─────────────────┘
                              │ (Dispatchers.IO)
                 ┌────────────▼─────────────────┐
                 │         Repository            │
                 │  SQLDelight Queries           │
                 └────────────┬─────────────────┘
                              │
                 ┌────────────▼─────────────────┐
                 │      SQLite (Local DB)        │
                 └──────────────────────────────┘
```

### Anexo III: Motor Algorítmico Astronómico Local

En lugar de depender de APIs externas con los consiguientes problemas de conectividad, latencia y cuotas, la aplicación calcula todos los eventos astronómicos **completamente en local** usando la librería **Astronomy Engine** (`io.github.cosinekitty:astronomy`).

El `AstroEventCalculator` calcula eclipses solares, conjunciones planetarias, fases lunares y otros fenómenos relevantes para un rango de fechas determinado:

```kotlin
// app/.../utils/AstroEventCalculator.kt (fragmento representativo)
val solar = searchGlobalSolarEclipse(startTime)
events.add(AstroEvent(
    name        = "Eclipse Solar",
    description = "Eclipse solar global (${solar.kind})",
    date        = df.format(Date(solar.peak.toMillisecondsSince1970())),
    timestamp   = solar.peak.toMillisecondsSince1970(),
    type        = EventType.CONJUNCTION
))
```

El resultado del cálculo es una lista de `AstroEvent`, un modelo de datos simple con `name`, `description`, `date`, `timestamp` y `type` (`EventType`), que es consumida tanto por el `HomeViewModel` (para mostrar los próximos 7 eventos) como por el `SocialEventsViewModel` (para la lista de 15 eventos al crear una quedada).

El `MoonCalculator` usa las mismas APIs de Astronomy Engine pero está especializado en el cálculo de la fase lunar actual, devolviendo:
- **`moonAge`**: Días transcurridos desde la última luna nueva (0–29.5 días).
- **`illumination`**: Porcentaje de superficie lunar iluminada (0.0–1.0).
- **`phaseName`**: Nombre de la fase en el idioma configurado (*"Luna Nueva"*, *"Cuarto Creciente"*, etc.).

### Anexo IV: Patrón de Navegación en Jetpack Compose

La navegación entre pantallas se gestiona con **Jetpack Navigation for Compose**, usando un `NavHost` con rutas tipadas como cadenas de texto. Cada pantalla sigue el patrón **Route + Screen**: la `*Route.kt` gestiona la obtención del `ViewModel` y su inyección en la composable pura `*Screen.kt`, que no tiene dependencias directas de Android:

```
NavHost
 ├── "home"         → HomeScreen
 ├── "calendar"     → Calendar
 ├── "event_detail/{eventId}/{eventName}/{eventDate}/{eventDesc}/{eventType}"
 │                  → EventDetailScreen
 ├── "social"       → SocialEventsScreen
 ├── "login"        → LoginScreen
 └── "register"     → RegisterScreen
```

La ruta de `EventDetailScreen` encapsula en la propia URL los parámetros del evento seleccionado, evitando la necesidad de un estado global compartido.

### Anexo V: Seguridad en la Autenticación Local

Dado que el sistema de autenticación es local (no existe un servidor de identidad), se aplicaron varias capas de seguridad:

| Capa | Mecanismo | Detalles |
|------|-----------|----------|
| **Hashing de contraseñas** | SHA-256 | La contraseña nunca se persiste ni transmite en texto plano |
| **Unicidad controlada** | Pre-check + UNIQUE SQL | Verificación doble: en repositorio (mensaje específico) y en esquema SQL (fallback) |
| **Validación cliente** | `RegisterViewModel` | Validaciones de formato de email, longitud mínima y coincidencia de contraseñas antes de tocar la BD |
| **Sealed classes de resultado** | `UserInsertResult` | Elimina la necesidad de capturar excepciones genéricas en la UI |

---

## 5. Conclusiones y Mejoras Futuras

Tras el desarrollo completo de AstroJournal, se ha logrado materializar una aplicación móvil Android que une de forma efectiva un **calendario astronómico de alta precisión científica** (apoyado en *Astronomy Engine*) con un entorno de interacción comunitaria a través del módulo Social, todo ello bajo una arquitectura limpia, extensible y lista para escalar.

### 5.1. Reflexión Técnica sobre las Decisiones Arquitectónicas

La decisión más determinante del proyecto fue adoptar **Kotlin Multiplatform** desde el inicio. Aunque supuso una curva de aprendizaje considerable —especialmente en la configuración del módulo compartido y la inicialización del driver de SQLDelight para Android— el resultado es un módulo `shared` que puede compilarse sin modificaciones para iOS en el futuro. Esta decisión obligó a un diseño forzosamente limpio: toda la lógica de persistencia y cálculo matemático quedó encapsulada en código Kotlin puro, sin contaminación de APIs específicas de Android.

La adopción del patrón **Repository + ViewModel + StateFlow** resultó ser la elección correcta para gestionar la reactividad. En pantallas complejas como `SocialEventsScreen` —que combina tres fuentes de datos distintas: eventos propios, eventos comunitarios y eventos astronómicos calculados localmente— la agrupación del estado en un único `SocialEventsUiState` eliminó problemas de sincronización que habrían sido difíciles de depurar con múltiples `LiveData` independientes.

El uso de `Mutex` en el `EventDetailViewModel` para serializar las operaciones de escritura fue una decisión necesaria para evitar condiciones de carrera cuando el usuario cambia rápidamente entre estados (observado/agendado) mientras las corrutinas anteriores aún no han terminado.

### 5.2. Dificultades Técnicas Encontradas

- **Configuración multiplataforma de SQLDelight**: La generación del driver SQLite para Android requiere una `DatabaseDriverFactory` específica que se inyecta desde el módulo Android, rompiendo la simetría del módulo compartido. Este patrón *expect/actual* de KMP tiene una documentación escasa para versiones recientes.
- **Gestión del Epoch en eventos astronómicos**: Astronomy Engine trabaja con su propio sistema de tiempo (días julianos), lo que requirió implementar funciones de conversión entre el tiempo de Astronomy Engine y el sistema de tiempo de Java (`toMillisecondsSince1970()`), especialmente sensible a errores de zona horaria UTC.
- **Concurrencia en el scrapbook**: La posibilidad de que el usuario abra el mismo evento desde múltiples rutas de navegación antes de que las operaciones de escritura completaran obligó a introducir el `Mutex` en el `EventDetailViewModel`.

### 5.3. Prospectiva y Mejoras Futuras

Gracias a la arquitectura Clean adoptada, las principales mejoras se pueden incorporar de forma incremental sin reescribir el sistema:

1. **Backend REST centralizado**: La dependencia de **Ktor Client** ya está evaluada en el proyecto. Sustituir las llamadas al `Repository` local por llamadas a una API REST (SpringBoot o Node.js) requeriría únicamente añadir un nuevo `Repository` que consuma la red, sin tocar la UI ni los `ViewModel`.

2. **Compilación para iOS**: El 100% del módulo `shared` es código Kotlin puro compatible con KMP para iOS. Solo se requiere implementar el `DatabaseDriverFactory` para iOS usando `NativeSqliteDriver`.

3. **Sistema multiusuario en eventos sociales**: Con un backend real, los `MeetupEvent` podrían sincronizarse entre dispositivos, creando una red social real de astrónomos aficionados.

4. **APOD (Astronomy Picture of the Day, NASA)**: La arquitectura ya está preparada para incorporar un nuevo `Repository` que consuma la API pública de la NASA y muestre la imagen astronómica diaria.

5. **Notificaciones push**: Implementar `WorkManager` de Android para lanzar notificaciones locales cuando un evento astronómico calculado esté próximo (dentro de 24 horas).

---

## 6. Reparto de Tareas y Roles dentro del Equipo

El trabajo colaborativo se apoyó en **Git con ramas funcionales por feature** (`feature/home`, `feature/database`, `feature/social`, `feature/auth`), lo que obligó a establecer un reparto bien delimitado y minimizó los conflictos de fusión.

### Integración y control de versiones

La estrategia de branching seguida fue la siguiente:
- `main`: Rama de producción, solo recibe merges de `develop` tras revisión.
- `develop`: Rama de integración continua.
- `feature/*`: Una rama por funcionalidad, mergeada a `develop` mediante Pull Request.

### Distribución funcional

| Área | Responsabilidades |
|------|------------------|
| **Frontend / UI Mobile** | Diseño del sistema visual y componentes reutilizables en Jetpack Compose (`Cards`, `BottomSheet`, `Carousels`). Implementación de la navegación con `NavHost`. Desarrollo de `ViewModels` reactivos (`HomeViewModel`, `EventDetailViewModel`). Pantallas: `HomeScreen`, `Calendar`, `EventDetailScreen`. |
| **Data Architecture & Core Logic** | Diseño del esquema relacional en ficheros `.sq` de SQLDelight. Implementación de los tres `Repository` (User, MeetupEvent, Collectible) en el módulo KMP. Algoritmo de cálculo de eventos astronómicos (`AstroEventCalculator`, `MoonCalculator`) usando Astronomy Engine. Pantallas: `SocialEventsScreen`, `LoginScreen`, `RegisterScreen`. |

---

## 7. Pruebas y Validación

Durante la consolidación del producto se ejecutaron varios niveles de validación funcional:

### 7.1. Testing Funcional y de Casos Límite (UI & Edge Cases)

Se verificaron flujos con errores inducidos deliberadamente:

- **Registro con email duplicado**: Se comprobó que la inserción de un email ya registrado actualiza el estado de la UI a `RegisterUiState.Error("Este email ya está registrado")`, mostrando el mensaje correcto en pantalla sin crashear la aplicación.
- **Contraseñas que no coinciden**: La validación en cliente (`password != repeatPassword`) intercepta el error antes de cualquier operación de base de datos, con respuesta inmediata en la UI.
- **Login con credenciales incorrectas**: `findByCredentials` devuelve `null` cuando el hash SHA-256 no coincide, lo que dispara `LoginUiState.Error("Email o contraseña incorrectos")`.

### 7.2. Pruebas de Integridad Referencial (Base de Datos Local)

A través del `MeetupEventRepository` se insertaron quedadas artificiales apuntando a nombres de eventos astronómicos concretos (ej. `linkedAstroEventName = "Eclipse Solar"`). La extracción posterior de los registros confirmó el funcionamiento del puente lógico entre entidades, y la UI renderizó correctamente la fusión de ambos datos en pantalla.

Se verificó también el comportamiento de la limpieza de duplicados en el `EventDetailViewModel`: el método `cleanupDuplicates()` selecciona el registro más completo (usando `sortedWith(compareByDescending { it.observed }.thenByDescending { it.notes?.length })`) y elimina los duplicados, comprobando que después de la limpieza la tabla `collectible` contiene exactamente un registro por `eventId`.

### 7.3. Calibrado del Motor Matemático Astronómico

Dado el alto nivel de complejidad en la capa algorítmica, se generaron baterías de validación manual para los cálculos de:

- **Fases lunares**: Se compararon los resultados de `MoonCalculator.getMoonPhaseInfo()` con las efemérides publicadas por el Observatorio Astronómico Nacional (OAN) para fechas conocidas, verificando una precisión de ±1 hora en el momento de las fases principales.
- **Eclipses y conjunciones**: Los eventos calculados por `AstroEventCalculator` se contrastaron con los datos de la NASA (NASA Eclipse Explorer) para las fechas del año en curso, con resultados consistentes.
- **Conversiones de zona horaria**: Se validó que la transformación desde el tiempo de Astronomy Engine (días julianos) a `java.util.Date` devuelve la fecha local correcta independientemente de la zona horaria del dispositivo (UTC, UTC+1, UTC+2).

---

## 8. Bibliografía Normalizada (APA 7ª edición)

* Android Developers. (2024). *Jetpack Compose UI App Development Toolkit*. Google. https://developer.android.com/compose

* Android Developers. (2024). *Guide to app architecture*. Google. https://developer.android.com/topic/architecture

* Android Developers. (2024). *Kotlin coroutines on Android*. Google. https://developer.android.com/kotlin/coroutines

* Android Developers. (2024). *Navigation with Compose*. Google. https://developer.android.com/develop/ui/compose/navigation

* Cash App. (2024). *SQLDelight: Generates typesafe Kotlin APIs from SQL*. Block, Inc. https://cashapp.github.io/sqldelight/

* CosineKitty. (2024). *Astronomy Engine for C/C++, C#, Python, JavaScript, AutoIT, and Kotlin* [Software]. GitHub. https://github.com/cosinekitty/astronomy

* JetBrains. (2024). *Kotlin Multiplatform: Share code between iOS and Android*. JetBrains s.r.o. https://kotlinlang.org/docs/multiplatform.html

* JetBrains. (2024). *Ktor: Asynchronous framework for creating Kotlin applications*. JetBrains s.r.o. https://ktor.io/

* JetBrains. (2024). *Kotlin coroutines guide*. JetBrains s.r.o. https://kotlinlang.org/docs/coroutines-guide.html

* Martin, R. C. (2017). *Clean architecture: A craftsman's guide to software structure and design*. Prentice Hall.

* Observatorio Astronómico Nacional. (2024). *Efemérides astronómicas*. IGN. https://www.ign.es/web/astronomia-efemerides

* NASA. (2024). *NASA Eclipse Explorer*. National Aeronautics and Space Administration. https://eclipse.gsfc.nasa.gov/
