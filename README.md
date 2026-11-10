# Pan, Puntos y Premios

Sistema web de pedidos y fidelización para un restaurante de sándwiches. Permite administrar clientes, productos, inventario, pedidos y pagos, acumular y canjear puntos, y generar reportes en PDF.

## Tecnologías

Java 17, Spring Boot 3.5 con WebFlux sobre Netty (sin Servlet), Thymeleaf, Bootstrap, MySQL 8 con JDBC y OpenPDF.

## Requisitos

Java 17, Maven 3.9 y MySQL 8.

## Cómo ejecutarlo

Crear la base de datos con los datos de prueba

    mysql -u root -p < database/proyecto_final.sql

Compilar y ejecutar

    cd sistema-web
    mvn clean package
    java -jar target/pan-puntos-premios.jar

Abrir en el navegador http://localhost:8080

Las credenciales de MySQL están en sistema-web/src/main/resources/application.properties

## Estructura

database contiene el script SQL
docs contiene el diagrama de clases y el diagrama entidad relación
sistema-web contiene la aplicación organizada en modelo, repositorio, servicio, controlador y vistas

## Datos de prueba

Clientes CLI-1001, CLI-1002, CLI-1003 y CLI-1004 (inactivo)

Tarjeta aprobada 4111 1111 1111 1111
Tarjeta rechazada 4000 0000 0000 0002

## API

Consulta de puntos de un cliente

    GET /api/clientes/{identificador}/puntos
