# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [2.0.0] - 2025-07-24
### Changed
- Se reemplaza el descubrimiento de servicios de Eureka por Consul en la configuración (`bootstrap.yml`) y dependencias (`pom.xml`).
- Se actualiza el nombre del proyecto a mayúsculas en `pom.xml`.
- Se agrega la dependencia `spring-boot-starter-actuator`.
- Se agregan `commons-lang3` y `httpclient` en `dependencyManagement`.
- Se instala `curl` en la imagen Docker principal.

### Removed
- Eliminados todos los modelos y archivos relacionados escritos en Kotlin.
- Eliminada la anotación `@EnableDiscoveryClient` y dependencias de Eureka.
- Eliminado el archivo `Dockerfile.local`.

### Added
- Nuevos tests unitarios en Java para controladores y repositorios.
- Nueva utilidad `StringUtils.java` para normalización de cadenas.

> _Fuente: análisis de `git diff HEAD`, historial de commits y cambios en `pom.xml` y `bootstrap.yml`._

## [1.0.0] - 2025-07-15
- Limpieza y simplificación de la clase principal `EtereaMigrationApplication`.
- Unificación y simplificación de los Dockerfiles (`Dockerfile`, `Dockerfile.local`).
- Eliminadas dependencias y plugins no necesarios (devtools, soporte nativo).

### Fixed
- Mejoras menores de robustez y limpieza de código.

## [Unreleased]

## [0.2.0] - 2025-07-12
### Added
- Mejorado el pipeline de CI/CD para construir y publicar automáticamente dos tipos de imágenes Docker: una nativa con GraalVM (`latest`) y una basada en JVM (`latest-jvm`).
### Changed
- Refactorización general y limpieza de código, eliminando dependencias y configuraciones de Kotlin que ya no estaban en uso.


## [0.1.1] - 2025-07-09
### Fixed
- Corregido `NullPointerException` al procesar órdenes donde la información del pagador es nula.
### Changed
- Mejorada la robustez del proceso de importación. El sistema ahora registra un error y continúa si los datos del pagador son incompletos, en lugar de detenerse.


## [0.1.0] - 2025-07-08
### Added
- feat(docs): add comprehensive diagrams and adapt workflow
- feat(docs): implement new documentation workflow and bump version
- feat(deps): update dependencies and improve documentation (#16)
### Fixed
- fix(ci): update deprecated GitHub Actions to latest versions
### Build
- build(release): bump version to 0.0.2

## [0.0.3] - 2025-07-04
### Fixed
- Cambiando bookingPersons null por 0
### Changed
- Cambiando update a none para ddl-auto
### Docs
- update dependency versions and documentation
- Delete docker-compose.yml

## [0.0.2] - 2025-06-30
### Fixed
- Cambiando bookingPersons null por 0
### Changed
- Cambiando update a none para ddl-auto
### Docs
- update dependency versions and documentation

## [0.0.1-SNAPSHOT] - 2025-06-16
### Changed
- Cambia book_duration por 0 si es null en OrderNoteWebService
- Eliminado archivo docker-compose.yml

## [0.0.1-SNAPSHOT] - 2025-05-20
### Changed
- Downgrade de versión de Java (cambios en Dockerfile, Dockerfile.local, pom.xml, y GitHub Actions)

## [0.0.1-SNAPSHOT] - 2025-05-20
### Added
- Mejoras en documentación y dependencias
- Nuevos workflows de GitHub Actions para páginas
- Scripts para generación de documentación y wiki
- Configuración de documentación con Jekyll
### Changed
- Actualización de dependencias
- Configuración de open-in-view en false en bootstrap.yml

## [0.0.1-SNAPSHOT] - 2024-12-21
### Changed
- Actualizada versión de Spring Boot Starter Parent a 3.4.1

## [0.0.1-SNAPSHOT] - 2024-12-05
### Changed
- Actualización de versiones de dependencias

## [0.0.1-SNAPSHOT] - 2024-12-02
### Changed
- Actualización de versiones de dependencias

## [0.0.1-SNAPSHOT] - 2024-11-14
### Changed
- Modificación en el estado de órdenes de venta web

## [0.0.1-SNAPSHOT] - 2024-10-22
### Changed
- Actualización del conector de MySQL

## [0.0.1-SNAPSHOT] - 2024-10-13
### Changed
- Actualización de versiones de dependencias

## [0.0.1-SNAPSHOT] - 2024-09-22
### Changed
- Actualización de versiones de dependencias

## [0.0.1-SNAPSHOT] - 2024-09-11
### Changed
- Modificación del puerto de la aplicación

## [0.0.1-SNAPSHOT] - 2024-09-08
### Changed
- Optimización del Dockerfile

## [0.0.1-SNAPSHOT] - 2024-08-31
### Changed
- Actualización de versiones de dependencias

## [0.0.1-SNAPSHOT] - 2024-08-22
### Added
- Implementación de sistema de logging

## [0.0.1-SNAPSHOT] - 2024-08-19
### Changed
- Eliminación de credenciales del archivo bootstrap.yml

## [0.0.1-SNAPSHOT] - 2024-08-13
### Added
- Mejoras en GitHub Actions
- Actualización del README.md

## [0.0.1-SNAPSHOT] - 2024-04-28
### Added
- Integración con Eureka

## [0.0.1-SNAPSHOT] - 2024-01-31
### Changed
- Modificación en la lógica de toma de datos de pago

## [0.0.1-SNAPSHOT] - 2024-01-24
### Changed
- Actualización de versión de Spring Boot

## [0.0.1-SNAPSHOT] - 2024-01-17
### Added
- Nuevo endpoint para obtener order_notes de los últimos 2 días

## [0.0.1-SNAPSHOT] - 2023-12-30
### Changed
- Migración a Java 21

## [0.0.1-SNAPSHOT] - 2023-12-28
### Changed
- Actualización de versión de Kotlin

## [0.0.1-SNAPSHOT] - 2023-12-23
### Changed
- Actualización de versión de Spring Boot

## [0.0.1-SNAPSHOT] - 2023-12-03
### Changed
- Ajuste en el formato de fecha para archivos de importación

## [0.0.1-SNAPSHOT] - 2023-11-28
### Changed
- Actualización de versiones de Spring y GitHub Actions

## [0.0.1-SNAPSHOT] - 2023-11-25
### Changed
- Migración a Java 21 en Dockerfile y GitHub Actions

## [0.0.1-SNAPSHOT] - 2023-11-20
### Added
- Nuevo endpoint para cargar datos de OrderNote

## [0.0.1-SNAPSHOT] - 2023-11-17
### Changed
- Modificación de collation en campos específicos

## [0.0.1-SNAPSHOT] - 2023-11-16
### Added
- Implementación de scheduling para captura de reservas online
- Pipeline de CI/CD
### Changed
- Modificación en la captura de customer_notes
- Cambio de zona horaria a UTC-4
- Reubicación de credenciales a properties
- Cambio en rutas para orders

## [0.0.1-SNAPSHOT] - 2023-11-15
### Added
- Implementación de parser para reservas web
- Creación del README.md

## [0.0.1-SNAPSHOT] - 2023-03-19
### Added
- Commit inicial del proyecto 