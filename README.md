# Corocora Mayorista

Sistema de aplicación web para la gestión de inventarios y pedidos de una empresa mayorista de productos.

## Descripción

Corocora Mayorista es una solución integral diseñada para automatizar y optimizar los procesos de negocio de empresas mayoristas, proporcionando una plataforma modular que se adapta a los diferentes roles dentro de la organización: clientes, transportadores y administración.

## Características Principales

- Solución basada en módulos acorde al papel dentro del negocio: cliente, transportador y organización
- Modelo de datos único y central compartido por todos los módulos
- Gestión de procesos de negocio embebido mediante Camunda BPM
- Autenticación de usuarios y rutas de APIs securizadas
- Generación automática de facturas en PDF con almacenamiento en la nube
- Arquitectura de microservicios con contenedores Docker

## Arquitectura y Tecnologías

### Backend
- Java Spring Boot 3.2.5 / 3.5.7
- Python Flask 3.0.0
- Camunda BPM 7.24.0

### Frontend
- HTML5
- CSS3
- JavaScript

### Base de Datos
- PostgreSQL

### Gestión de Dependencias
- Maven
- pip (Python)

### Containerización
- Docker
- Docker Compose

### Almacenamiento
- Cloudflare R2 (almacenamiento de facturas)

## Estructura del Proyecto

```
corocoraLastVer/
├── BPM-Engine/                    # Motor de procesos Camunda
│   ├── src/main/java/            # Modelos de datos, delegates y lógica de procesos
│   ├── src/main/resources/
│   │   ├── bpmn/                 # Diagramas de procesos BPMN
│   │   ├── dmn/                  # Reglas de decisión DMN
│   │   └── forms/                # Formularios Camunda
│   └── pom.xml
├── corocora-organizacion/         # Módulo de administración
│   ├── src/main/java/            # Servicios, controladores y vistas
│   └── pom.xml
├── corocora-proveedores/          # Módulo de proveedores
│   └── pom.xml
├── corocota-clientes/             # Módulo de clientes
│   ├── src/main/java/            # Servicios, controladores y vistas
│   └── pom.xml
├── facturaGenerator/              # Microservicio de generación de facturas
│   ├── app.py                    # Aplicación Flask
│   ├── requirements.txt
│   ├── Dockerfile
│   └── logofactura.png
├── postgres_data/                 # Datos persistentes de PostgreSQL
├── docker-compose.yml
├── Dockerfile
└── pom.xml                        # POM principal (multi-módulo)
```

### Componentes del Proyecto

#### BPM-Engine (Puerto 8090)
Motor central de procesos de negocio que contiene:
- Modelos de datos compartidos por todos los módulos
- Delegates para la ejecución de tareas automatizadas
- Definiciones de procesos BPMN
- Configuración de Camunda BPM

#### corocora-organizacion (Puerto 8091)
Módulo de administración que incluye:
- Servicios REST para gestión de inventarios
- Controladores de APIs
- Vistas y funcionalidades administrativas


#### corocota-clientes (Puerto 8092)
Módulo de clientes que proporciona:
- Servicios REST para gestión de pedidos
- Controladores de APIs
- Vistas orientadas al cliente final

#### facturaGenerator (Puerto 5000)
Microservicio transaccional en Python que:
- Recibe una cadena de caracteres con información de productos
- Genera facturas en formato PDF
- Sube las facturas a Cloudflare R2
- Retorna la URL pública del documento almacenado

## Requisitos Previos

- Java Development Kit (JDK) 21 o superior
- Maven 3.6+
- Python 3.8+
- Docker y Docker Compose
- Cuenta de Cloudflare R2 (para almacenamiento de facturas)

## Variables de Entorno

### facturaGenerator
Crear un archivo `.env` en el directorio raiz del proyecto con las siguientes variables:

```env
R2_ACCESS_KEY_ID
R2_SECRET_ACCESS_KEY
R2_ENDPOINT_URL
R2_BUCKET_NAME
R2_PUBLIC_URL
```
Estas credenciales son necesarias para la integración con Cloudflare R2 y el almacenamiento de facturas generadas.

## Configuración del Proyecto

### Fase de Desarrollo

#### 1. Levantar la Base de Datos

Iniciar el contenedor de PostgreSQL y el servicio de generación de facturas:

```cmd
docker-compose up -d
```

Esto levantará:
- PostgreSQL en el puerto 5433
- facturaGenerator en el puerto 5000

#### 2. Configurar Productos

Los productos ofrecidos por la empresa se definen directamente en la base de datos PostgreSQL en la tabla `productos` del esquema `postgres`. Asegúrese de que esta tabla esté poblada antes de continuar.

#### 3. Ejecutar Módulos Spring Boot

Los proyectos BPM-Engine y los módulos corocora requieren que la base de datos ya esté levantada.

Ejecutar en el siguiente orden:

```cmd
# 1. BPM-Engine (Motor Camunda)
cd BPM-Engine
mvn spring-boot:run

# 2. corocora-organizacion (en otra terminal)
cd corocora-organizacion
mvn spring-boot:run

# 3. corocota-clientes (en otra terminal)
cd corocota-clientes
mvn spring-boot:run
```

Alternativamente, compilar desde la raíz del proyecto:

```cmd
mvn clean install
```

#### 4. Crear Usuario Inicial

Si no existe un usuario con el correo "camunda@gmail.com", créelo llamando al siguiente endpoint:

**POST** `http://localhost:8091/api/clientes/crear`

```json
{
    "nombres": "camunda",
    "documento": 11111111,
    "email": "camunda@gmail.com",
    "password": "camunda"
}
```

## Endpoints y Accesos

### Camunda BPM Cockpit
- URL: `http://localhost:8090`
- Usuario: `demo`
- Contraseña: `demo`

### API Endpoints

#### BPM-Engine
- Base URL: `http://localhost:8090`
- Engine REST API: `http://localhost:8090/engine-rest/`

#### corocora-organizacion
- Base URL: `http://localhost:8091`


#### corocota-clientes
- Base URL: `http://localhost:8092`

#### facturaGenerator
- Base URL: `http://localhost:5000`

### Base de Datos
- Host: `localhost`
- Puerto: `5433`
- Database: `postgres`
- Usuario: `postgres`
- Contraseña: `0000`

## Notas de Desarrollo

- La configuración actual está optimizada para entorno de desarrollo
- `spring.jpa.hibernate.ddl-auto=update` creará/actualizará automáticamente el esquema de base de datos
- Los logs SQL están habilitados para depuración
- Asegúrese de que los puertos 5433, 8090, 8091, 8092 y 5000 estén disponibles

## Tecnologías Adicionales

- Lombok: Reducción de código boilerplate
- Hibernate/JPA: ORM para persistencia de datos
- ReportLab: Generación de PDFs en Python
- boto3: Cliente AWS S3 compatible (Cloudflare R2)
- num2words: Conversión de números a palabras en facturas



