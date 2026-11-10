# Pan, Puntos y Premios — Sistema de Pedidos y Fidelización

Aplicación web en Java con arquitectura **MVC** para un restaurante de sándwiches: productos, inventario, pedidos, pagos en efectivo o con tarjeta, fidelización con puntos, canjes y reportes en PDF.

## Tecnologías

| Componente | Versión |
|---|---|
| Java (JDK) | 17 LTS |
| Maven | 3.9.x |
| Spring Boot | 3.5.16 (Spring WebFlux sobre Netty, **sin Servlet**) |
| Vistas | Thymeleaf + Bootstrap 5.3.3 (WebJars) |
| Persistencia | MySQL 8.0, JDBC con `PreparedStatement` y pool HikariCP |
| PDF | OpenPDF 1.3.43 |

La aplicación no usa la API de Servlet ni Tomcat: el servidor es Reactor Netty y los controladores son `@Controller` de Spring WebFlux. Las operaciones JDBC, que son bloqueantes, se ejecutan en un pool de hilos dedicado (`ConfiguracionWeb`) y no en el event loop.

## Estructura

```
PROYECTO/
├── database/proyecto_final.sql      Script de creación de tablas y datos de prueba
├── docs/
│   ├── diagrama-clases.md           Diagrama de clases UML (Mermaid)
│   └── diagrama-entidad-relacion.md Diagrama entidad-relación (Mermaid)
└── sistema-web/                     Aplicación web (Maven)
    └── src/main/java/com/panpuntos/
        ├── modelo/          Entidades de dominio (POO: herencia, polimorfismo, composición)
        ├── repositorio/     Interfaces de acceso a datos
        │   └── jdbc/        Implementaciones JDBC (PreparedStatement)
        ├── servicio/        Lógica de negocio y transacciones
        │   └── reporte/     Generadores de reportes y exportación PDF
        ├── controlador/
        │   ├── web/         Controladores MVC (vistas Thymeleaf)
        │   └── api/         API REST (consulta de puntos)
        ├── dto/             Formularios con validación
        ├── excepcion/       Jerarquía de excepciones de negocio
        ├── configuracion/   Configuración de WebFlux
        └── util/            Utilidades de formato
```

## Puesta en marcha

1. Crear la base de datos con los datos de prueba:
   ```bash
   mysql -u root -p < database/proyecto_final.sql
   ```
2. Revisar las credenciales en `sistema-web/src/main/resources/application.properties`. También se pueden sobrescribir con las variables de entorno `DB_HOST`, `DB_PUERTO`, `DB_NOMBRE`, `DB_USUARIO` y `DB_CLAVE`.
3. Compilar, ejecutar las pruebas y arrancar:
   ```bash
   cd sistema-web
   mvn clean package
   java -jar target/pan-puntos-premios.jar
   ```
   También puede usar `mvn spring-boot:run`.
4. Abrir http://localhost:8080

## Módulos

| Ruta | Funcionalidad |
|---|---|
| `/clientes` | Registrar, buscar, modificar y activar o desactivar clientes. Muestra el historial de pedidos, de puntos y de canjes. Un cliente con historial no se elimina: se desactiva. |
| `/productos` | Sándwiches, bebidas, acompañamientos y menús. Cada menú se compone de un sándwich, una bebida y un acompañamiento, y tiene precio propio. |
| `/inventario` | Abastecimiento con cantidad positiva e historial de movimientos (entrada, venta y canje). |
| `/pedidos` | Pedidos con varios productos. Se verifica la existencia, se cobra en efectivo o con tarjeta y el pedido pasa por los estados PENDIENTE, PAGADO, ENTREGADO o ANULADO. Genera comprobante en pantalla y en PDF. |
| `/fidelizacion` | Consulta de saldo, canje de recompensas y mantenimiento del catálogo de recompensas. |
| `/reportes` | Seis reportes exportables a PDF. |
| `/api/clientes/{identificador}/puntos` | API REST en JSON para consultar el saldo de puntos (pensada para la app Android). |

## Reglas de negocio implementadas

- **Puntos:** `P = (S × 2) + (M × 8)`. Solo se acreditan en pedidos pagados que tienen cliente. Las bebidas y los acompañamientos vendidos por separado no suman puntos. Se calculan con polimorfismo: cada subclase de `Producto` sobrescribe `calcularPuntos`.
- **Menús:** al vender un menú se descuenta una unidad de cada componente, nunca del menú. Las unidades disponibles de un menú son las del componente con menos existencia.
- **Pagos:** el pago en efectivo calcula el cambio y rechaza montos insuficientes. El pago con tarjeta es una simulación: se aprueba si el número pasa la validación Luhn y se rechaza por fondos insuficientes si inicia con `4000`. Siempre genera un número de referencia.
- **Consistencia:** el cobro se hace en una sola transacción. Bloquea el pedido y los productos (`SELECT ... FOR UPDATE`), vuelve a validar la existencia, descuenta el inventario, registra el pago, acredita los puntos y cambia el estado. Si algo falla, se revierte todo.
- **Canjes:** se valida que el cliente exista y esté activo, que tenga saldo suficiente, que la recompensa esté activa y que haya unidades del producto. Si alguna condición falla, se rechaza sin modificar puntos ni inventario. El canje no genera puntos.
- **Anulación:** solo se anulan pedidos pendientes. Un pedido pagado no puede anularse.

### Tarjetas de prueba

| Número | Resultado |
|---|---|
| `4111 1111 1111 1111` | Aprobada |
| `5555 5555 5555 4444` | Aprobada |
| `4000 0000 0000 0002` | Rechazada: fondos insuficientes |
| `1234 5678` | Rechazada: número inválido |

### Datos de prueba

| Cliente | Puntos | Estado |
|---|---|---|
| CLI-1001 Juan Pérez | 2 | Activo |
| CLI-1002 Ana López | 56 | Activo (alcanza para un menú gratis) |
| CLI-1003 Carlos Méndez | 0 | Activo |
| CLI-1004 Lucía Ramírez | 0 | Inactivo |

El Agua Pura está agotada, y los Aros de Cebolla y el Sándwich Vegetariano tienen existencia baja.

## Reportes (PDF)

1. Ventas por período, desglosadas por efectivo y tarjeta.
2. Productos más vendidos, incluidos los componentes de menú.
3. Productos agotados o con existencia de 5 unidades o menos.
4. Puntos acumulados por cliente.
5. Historial de canjes.
6. Ventas de menús completos.

Para agregar un reporte basta con crear una clase que implemente `GeneradorReporte` y anotarla con `@Component`; se registra automáticamente.

## Pruebas

```bash
cd sistema-web
mvn test
```
