## Proyecto Final – Programación II

Proyecto: Pan, Puntos y Premios — Sistema de Pedidos y Fidelización

## Objetivo

Desarrollar una aplicación web en Java, estructurada bajo el patrón de diseño MVC visto en clase, con persistencia en MySQL, que administre productos, pedidos y pagos de un restaurante de sándwiches, e implemente un programa de fidelización de puntos con acumulación y canje.

## Descripción del Proyecto

El restaurante vende sándwiches individuales y menús completos, y necesita controlar sus productos y ventas diarias. Cada cliente acumula puntos por cada sándwich (2 pts) o menú completo (8 pts) pagado, los cuales puede canjear posteriormente por productos gratuitos si cuenta con saldo suficiente y hay unidades disponibles.

El sistema debe aceptar pagos en efectivo y tarjeta, verificar existencias antes de confirmar cada venta, y mantener siempre consistente la información de inventario y puntos.

## Requerimientos del Sistema

- a) Gestión de clientes Registrar, buscar y modificar clientes, con saldo de puntos inicial en cero y sin identificadores duplicados. Debe mostrarse el historial de pedidos y de movimientos de puntos de cada cliente. No se elimina físicamente un cliente con compras registradas; se admite su desactivación.

- b) Gestión de productos Registrar, consultar, modificar y desactivar productos (sándwiches, bebidas, acompañamientos), cada uno con código único, categoría, nombre, precio y existencia, sin valores negativos. No se permite vender un producto desactivado o sin unidades disponibles.

- c) Abastecimiento de inventario Ingresar nuevas unidades a un producto existente, validando cantidad positiva, y registrar cada movimiento en un historial con fecha, producto, cantidad y tipo de movimiento.

- d) Gestión de pedidos Un pedido puede incluir uno o varios productos: sándwiches individuales, bebidas, acompañamientos y menús completos. Cada menú está compuesto por un sándwich, una bebida y un acompañamiento, con precio propio de catálogo (no la suma de sus componentes); al vender un menú se descuenta una unidad de cada componente, nunca una unidad adicional del menú como si fuera un cuarto producto. Antes de confirmar el pedido se verifica disponibilidad de cada producto; si falta inventario, se informa la cantidad disponible y se rechaza la confirmación.

- e) Procesamiento de pagos y estados del pedido El pedido nace en estado PENDIENTE. Al cobrarse, se admite pago en efectivo (el sistema calcula el cambio y rechaza montos insuficientes) o tarjeta (simulación de aprobación/rechazo con número de referencia). Un pago aprobado revalida existencias, descuenta inventario, acredita los puntos correspondientes y cambia el pedido a PAGADO, generando el comprobante; si el pago es rechazado o el inventario es insuficiente, no se registra venta ni se acreditan puntos. Un pedido pagado puede pasar a ENTREGADO; un pedido pendiente puede ANULARSE sin afectar inventario ni puntos, pero un pedido ya pagado no puede anularse desde el módulo de ventas.

- f) Sistema de fidelización Los puntos se acreditan únicamente sobre pedidos pagados, según P = (S × 2) + (M × 8), donde S es la cantidad de sándwiches individuales y M la de menús completos (bebidas y acompañamientos comprados por separado no otorgan puntos). El catálogo de recompensas (bebida gratuita: 10 pts, acompañamiento gratuito: 15 pts, sándwich gratuito: 30 pts, menú gratuito: 50 pts) vive en base de datos. Un canje solo se ejecuta si el cliente existe y está activo, tiene saldo suficiente, la recompensa está activa y hay unidades disponibles del producto asociado; de lo contrario se rechaza sin modificar puntos ni inventario. El canje no genera puntos adicionales.


- g) Base de datos MySQL, conexión desde Java mediante JDBC, con todas las operaciones ejecutadas mediante PreparedStatement.

- h) Arquitectura del sistema La aplicación debe ser web (no de escritorio) y estar estructurada siguiendo el patrón de diseño MVC visto en clase, separando claramente el acceso a datos, la lógica de negocio y la presentación.

- i) Programación orientada a objetos El sistema deberá demostrar el uso funcional de programación orientada a objetos: encapsulamiento, herencia, polimorfismo, manejo de colecciones, composición y manejo de excepciones. Su aplicación debe ser funcional y evidente en el comportamiento del programa, no limitarse a declararse en el código.

- j) Reportes (exportables en PDF) Ventas por período (desglosadas por efectivo/tarjeta) · Productos más vendidos (incluyendo componentes de menú) · Productos agotados o con existencia ≤ 5 unidades · Puntos acumulados por cliente · Historial de canjes · Ventas de menús completos.

- k) Aplicación Android El sistema deberá incluir una aplicación Android (nativa o que consuma una API REST) que permita a un cliente consultar su saldo de puntos acumulados ingresando su identificador.

- l) Entregables Código fuente en repositorio Git con 5 commits en fechas distintas y de todos los integrantes · Script SQL de creación de tablas y datos de prueba · Diagrama de clases UML y diagrama entidad-relación.



El proyecto debe desarrollarse como aplicación web en Java, siguiendo el patrón MVC visto en clase, con MySQL como motor de base de datos. Se debe demostrar POO, manejo de colecciones y operaciones sobre base de datos de forma funcional. El código debe entregarse limpio de comentarios.


MYSQL
db.usuario=root
db.clave=juanct76

database llama la DB como   proyecto_final