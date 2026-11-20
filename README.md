# Operación Alquimia — Protocolo Savitar

Solución de software desarrollada en **Java 17** con **Spring Boot**, diseñada e implementada aplicando **Domain-Driven Design (DDD)** y **Arquitectura Hexagonal (Puertos y Adaptadores)**, siguiendo exactamente la estructura y convenciones arquitectónicas del proyecto de referencia `nexMarket`.

---

## 1. Contexto del Problema

El Equipo Flash ha interceptado las comunicaciones fragmentadas de Alchemy y necesita reconstruir los mensajes originales y triangular la ubicación exacta `(X, Y)` de su laboratorio secreto en Central City a partir de la señal de tres dispositivos de vigilancia:

| Dispositivo | Identificadores Aceptados | Posición Fija `(X, Y)` |
| :--- | :--- | :--- |
| **STAR Labs** | `"STAR Labs"`, `"STAR_LABS"`, `"starlabs"` | `[-500, -200]` |
| **CCPD** | `"CCPD"`, `"ccpd"` | `[100, -100]` |
| **S.T.A.R. Satellite** | `"S.T.A.R. Satellite"`, `"STAR_SATELLITE"`, `"satellite"` | `[500, 100]` |

---

## 2. Arquitectura del Proyecto (DDD + Arquitectura Hexagonal)

Siguiendo la arquitectura de `nexMarket`, el proyecto aísla completamente la lógica de negocio dentro de la capa `domain` (POJOs puros sin dependencias de Spring, HTTP ni persistencia externa) y conecta el exterior mediante **Puertos (`ports/in` y `ports/out`)** y **Adaptadores (`adapters/rest`, `adapters/useCases`, `adapters/persistence/inmemory`)**:

```text
operacionAlquimia/
├── Dockerfile
├── docker-compose.yml
├── mvnw / mvnw.cmd
├── pom.xml
└── src/
    ├── main/
    │   ├── java/application/
    │   │   ├── OperacionAlquimiaApplication.java
    │   │   ├── domain/
    │   │   │   ├── models/
    │   │   │   │   ├── Position.java
    │   │   │   │   ├── SurveillanceDevice.java
    │   │   │   │   └── LaboratoryIntelligence.java
    │   │   │   ├── valueObjects/
    │   │   │   │   └── SurveillanceDeviceType.java
    │   │   │   ├── exceptions/
    │   │   │   │   ├── DomainException.java
    │   │   │   │   ├── UnknownDeviceException.java
    │   │   │   │   ├── EntityNotFoundException.java
    │   │   │   │   ├── InsufficientInformationException.java
    │   │   │   │   ├── TrilaterationCalculationException.java
    │   │   │   │   └── MessageReconstructionException.java
    │   │   │   ├── ports/
    │   │   │   │   ├── in/
    │   │   │   │   │   ├── SurveillanceOperationsPort.java
    │   │   │   │   │   ├── ProcessCompleteInformationUseCase.java
    │   │   │   │   │   ├── RegisterIndividualDeviceUseCase.java
    │   │   │   │   │   ├── ConsultAccumulatedInformationUseCase.java
    │   │   │   │   │   ├── ReplaceDeviceInformationUseCase.java
    │   │   │   │   │   ├── PartialUpdateDeviceInformationUseCase.java
    │   │   │   │   │   └── DeleteDeviceInformationUseCase.java
    │   │   │   │   └── out/
    │   │   │   │       └── SurveillanceDeviceRepositoryPort.java
    │   │   │   └── services/
    │   │   │       ├── trilateration/
    │   │   │       │   └── TrilaterationService.java
    │   │   │       ├── reconstruction/
    │   │   │       │   └── MessageReconstructionService.java
    │   │   │       └── surveillance/
    │   │   │           ├── ProcessCompleteInformationService.java
    │   │   │           ├── RegisterIndividualDeviceService.java
    │   │   │           ├── ConsultAccumulatedInformationService.java
    │   │   │           ├── ReplaceDeviceInformationService.java
    │   │   │           ├── PartialUpdateDeviceInformationService.java
    │   │   │           └── DeleteDeviceInformationService.java
    │   │   ├── adapters/
    │   │   │   ├── useCases/
    │   │   │   │   └── SurveillanceOperationsUseCaseImpl.java
    │   │   │   ├── rest/
    │   │   │   │   ├── controllers/
    │   │   │   │   │   └── SurveillanceController.java
    │   │   │   │   ├── dtos/
    │   │   │   │   │   ├── requests/
    │   │   │   │   │   └── responses/
    │   │   │   │   ├── mappers/
    │   │   │   │   │   └── SurveillanceRestMapper.java
    │   │   │   │   └── exception/
    │   │   │   │       ├── ErrorResponse.java
    │   │   │   │       └── GlobalExceptionHandler.java
    │   │   │   └── persistence/
    │   │   │       └── inmemory/
    │   │   │           ├── entities/
    │   │   │           │   └── SurveillanceDeviceMemoryEntity.java
    │   │   │           ├── repositories/
    │   │   │           │   └── InMemorySurveillanceDeviceRepository.java
    │   │   │           ├── mappers/
    │   │   │           │   └── SurveillanceDeviceMemoryMapper.java
    │   │   │           └── adapters/
    │   │   │               └── SurveillanceDeviceInMemoryAdapter.java
    │   │   └── infrastructure/
    │   │       └── config/
    │   │           └── DomainServicesConfig.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/application/
            ├── domain/services/TrilaterationAndReconstructionDomainTest.java
            └── adapters/rest/controllers/SurveillanceControllerIntegrationTest.java
```

---

## 3. Ejecución en Cualquier Computador

El proyecto incluye **Maven Wrapper (`mvnw` / `mvnw.cmd`)** y configuración de **Docker (`Dockerfile` y `docker-compose.yml`)**, además de almacenamiento 100% en memoria (`ConcurrentHashMap` thread-safe), por lo que **no requiere instalar bases de datos ni servicios externos**.

### Opción A: Con Maven Wrapper (Windows / Linux / macOS)

En **Windows (PowerShell / CMD)**:
```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

En **Linux / macOS**:
```bash
./mvnw clean test
./mvnw spring-boot:run
```

### Opción B: Con Docker (Sin necesidad de tener Java ni Maven instalados)

```bash
docker compose up --build
```

El servidor quedará disponible en `http://localhost:8080`.

---

## 4. Especificación de Endpoints REST (Requerimientos 2.1 a 2.6)

### 2.1. Procesamiento de información completa (`POST /api/v1/surveillance` o `POST /topsecret`)
Recibe simultáneamente los tres dispositivos, calcula la posición del laboratorio por trilateración 2D y reconstruye el mensaje ignorando cadenas vacías (`""`).

**Request:**
```json
{
  "devices": [
    {
      "name": "STAR Labs",
      "distance": 100.0,
      "message": ["este", "", "", "mensaje", ""]
    },
    {
      "name": "CCPD",
      "distance": 115.5,
      "message": ["", "es", "", "", "secreto"]
    },
    {
      "name": "S.T.A.R. Satellite",
      "distance": 142.7,
      "message": ["este", "", "un", "", ""]
    }
  ]
}
```

**Response (`200 OK`):**
```json
{
  "position": {
    "x": -487.29,
    "y": 1557.01
  },
  "message": "este es un mensaje secreto"
}
```

---

### 2.2. Registro de información individual (`POST /api/v1/surveillance/devices/{deviceId}` o `POST /api/v1/surveillance/devices`)
Registra temporalmente en memoria la información de un dispositivo de vigilancia de forma independiente y en cualquier orden.

**Request (`POST /api/v1/surveillance/devices/STAR_LABS`):**
```json
{
  "distance": 100.0,
  "message": ["este", "", "", "mensaje", ""]
}
```

---

### 2.3. Consulta y procesamiento de información acumulada (`GET /api/v1/surveillance` o `GET /topsecret_split`)
Procesa los datos acumulados en memoria. Si aún no están registrados los 3 dispositivos, retorna `404 Not Found` indicando que no existen datos suficientes.
- Para listar los dispositivos registrados actualmente: `GET /api/v1/surveillance/devices`
- Para consultar un dispositivo específico: `GET /api/v1/surveillance/devices/{deviceId}`

---

### 2.4. Reemplazo completo de información de un dispositivo (`PUT /api/v1/surveillance/devices/{deviceId}`)
Sustituye completamente la distancia y el fragmento del mensaje de un dispositivo previamente registrado, sin afectar a los demás dispositivos.

**Request (`PUT /api/v1/surveillance/devices/CCPD`):**
```json
{
  "distance": 115.5,
  "message": ["", "es", "", "", "secreto"]
}
```

---

### 2.5. Actualización parcial de información de un dispositivo (`PATCH /api/v1/surveillance/devices/{deviceId}`)
Actualiza únicamente la distancia (`distance`) y/o el fragmento del mensaje (`message`), conservando intactos los campos no enviados.

**Request (`PATCH /api/v1/surveillance/devices/CCPD`):**
```json
{
  "distance": 120.0
}
```

---

### 2.6. Eliminación de información de un dispositivo (`DELETE /api/v1/surveillance/devices/{deviceId}`)
Elimina de memoria únicamente la información del dispositivo indicado, conservando la información de los demás dispositivos (`204 No Content`).
