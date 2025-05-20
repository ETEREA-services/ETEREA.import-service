# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2024.12.21] - 2024-12-21
### Changed
- Actualizada versión de Spring Boot Starter Parent a 3.4.5
- Actualizada versión de Java a 24
- Actualizada versión de Kotlin a 2.1.20
- Actualizada versión de Spring Cloud a 2024.0.1
- Actualizada versión del conector MySQL a 9.3.0
- Actualizada versión de OpenAPI a 2.8.8
### Added
- Agregada dependencia de Caffeine para caché
- Agregada dependencia de Spring Boot Starter Cache
### Changed
- Configurado open-in-view en false en bootstrap.yml

## [2024.12.05] - 2024-12-05
### Changed
- Actualización de versiones de dependencias

## [2024.12.02] - 2024-12-02
### Changed
- Actualización de versiones de dependencias

## [2024.11.14] - 2024-11-14
### Changed
- Modificación en el estado de órdenes de venta web

## [2024.10.22] - 2024-10-22
### Changed
- Actualización del conector de MySQL

## [2024.10.13] - 2024-10-13
### Changed
- Actualización de versiones de dependencias

## [2024.09.22] - 2024-09-22
### Changed
- Actualización de versiones de dependencias

## [2024.09.11] - 2024-09-11
### Changed
- Modificación del puerto de la aplicación

## [2024.09.08] - 2024-09-08
### Changed
- Optimización del Dockerfile

## [2024.08.31] - 2024-08-31
### Changed
- Actualización de versiones de dependencias

## [2024.08.22] - 2024-08-22
### Added
- Implementación de sistema de logging

## [2024.08.19] - 2024-08-19
### Changed
- Eliminación de credenciales del archivo bootstrap.yml

## [2024.08.13] - 2024-08-13
### Added
- Mejoras en GitHub Actions
- Actualización del README.md

## [2024.04.28] - 2024-04-28
### Added
- Integración con Eureka

## [2024.01.31] - 2024-01-31
### Changed
- Modificación en la lógica de toma de datos de pago

## [2024.01.24] - 2024-01-24
### Changed
- Actualización de versión de Spring Boot

## [2024.01.17] - 2024-01-17
### Added
- Nuevo endpoint para obtener order_notes de los últimos 2 días

## [2023.12.30] - 2023-12-30
### Changed
- Migración a Java 21

## [2023.12.28] - 2023-12-28
### Changed
- Actualización de versión de Kotlin

## [2023.12.23] - 2023-12-23
### Changed
- Actualización de versión de Spring Boot

## [2023.12.03] - 2023-12-03
### Changed
- Ajuste en el formato de fecha para archivos de importación

## [2023.11.28] - 2023-11-28
### Changed
- Actualización de versiones de Spring y GitHub Actions

## [2023.11.25] - 2023-11-25
### Changed
- Migración a Java 21 en Dockerfile y GitHub Actions

## [2023.11.20] - 2023-11-20
### Added
- Nuevo endpoint para cargar datos de OrderNote

## [2023.11.17] - 2023-11-17
### Changed
- Modificación de collation en campos específicos

## [2023.11.16] - 2023-11-16
### Added
- Implementación de scheduling para captura de reservas online
- Pipeline de CI/CD
### Changed
- Modificación en la captura de customer_notes
- Cambio de zona horaria a UTC-4
- Reubicación de credenciales a properties
- Cambio en rutas para orders

## [2023.11.15] - 2023-11-15
### Added
- Implementación de parser para reservas web
- Creación del README.md

## [2023.03.19] - 2023-03-19
### Added
- Commit inicial del proyecto 