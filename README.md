# ETEREA.import-service

[![ETEREA.import-service CI](https://github.com/ETEREA-services/ETEREA.import-service/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/ETEREA-services/ETEREA.import-service/actions/workflows/maven.yml)
[![Java](https://img.shields.io/badge/Java-24-blue.svg)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-green.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-blue.svg)](https://spring.io/projects/spring-cloud)
[![MySQL](https://img.shields.io/badge/MySQL-9.3.0-blue.svg)](https://www.mysql.com)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-2.8.9-green.svg)](https://www.openapis.org)

## Descripción

Servicio de importación para el sistema ETEREA, desarrollado con Spring Boot. Este servicio se encarga de la gestión y procesamiento de importaciones de datos en el sistema.

## Características Principales

- Procesamiento de importaciones de datos
- Integración con servicios externos vía SFTP
- Caché de alto rendimiento con Caffeine
- Documentación de API con OpenAPI/Swagger
- Integración con Eureka para service discovery
- Seguridad implementada con Spring Security

## Tecnologías Utilizadas

- **Java 24**: Lenguaje base del proyecto
- **Spring Boot 3.5.3**: Framework principal
- **Spring Cloud 2025.0.0**: Para microservicios
- **MySQL 9.3.0**: Base de datos
- **Caffeine**: Caché en memoria
- **Spring Security**: Seguridad y autenticación
- **OpenAPI 2.8.9**: Documentación de API
- **JSch 0.1.55**: Cliente SFTP

## Documentación

- [Documentación de API](https://eterea-services.github.io/ETEREA.import-service/)
- [Wiki del Proyecto](https://github.com/ETEREA-services/ETEREA.import-service/wiki)
- [Guía de Despliegue](https://eterea-services.github.io/ETEREA.import-service/deployment-guide.html)
- [Guía de Desarrollo](https://eterea-services.github.io/ETEREA.import-service/development-guide.html)

## Configuración

El servicio se configura mediante el archivo `bootstrap.yml`. Las principales configuraciones incluyen:

- Puerto de la aplicación (por defecto: 8280)
- Configuración de Eureka (puerto: 8761)
- Configuración de la base de datos MySQL
- Configuración de SFTP
- Configuración de logging

## Desarrollo

Para comenzar a desarrollar:

1. Clonar el repositorio
2. Configurar las variables de entorno necesarias
3. Ejecutar `mvn clean install`
4. Iniciar el servicio con `mvn spring-boot:run`

## CI/CD

El proyecto utiliza GitHub Actions para:
- Construcción y pruebas automáticas
- Despliegue de documentación
- Actualización de la wiki
- Generación de imágenes Docker

## Licencia

Este proyecto está bajo la licencia [LICENSE](LICENSE).

---

## Imágenes Docker

El pipeline de CI/CD de este proyecto construye y publica automáticamente dos versiones de la imagen Docker en Docker Hub, optimizadas para diferentes casos de uso:

1.  **Imagen Nativa (GraalVM):**
    - **Descripción:** Un ejecutable nativo compilado con GraalVM. Ofrece un arranque casi instantáneo y un consumo de memoria muy bajo.
    - **Etiquetas:** `latest`, `<version>`, `<major>.<minor>`, `<commit-sha>`
    - **Uso recomendado:** Entornos de producción, especialmente en arquitecturas de microservicios, serverless o donde la eficiencia de recursos es crítica.

2.  **Imagen JVM:**
    - **Descripci��n:** Una imagen tradicional basada en una Java Virtual Machine (JVM). Es robusta y universalmente compatible.
    - **Etiquetas:** `latest-jvm`, `<version>-jvm`, `<major>.<minor>-jvm`, `<commit-sha>-jvm`
    - **Uso recomendado:** Desarrollo, depuración o entornos donde no se requiere el máximo rendimiento de arranque.