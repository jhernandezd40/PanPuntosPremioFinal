CREATE DATABASE IF NOT EXISTS proyecto_final CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE proyecto_final;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS movimiento_puntos;
DROP TABLE IF EXISTS canje;
DROP TABLE IF EXISTS recompensa;
DROP TABLE IF EXISTS pago;
DROP TABLE IF EXISTS detalle_pedido;
DROP TABLE IF EXISTS pedido;
DROP TABLE IF EXISTS movimiento_inventario;
DROP TABLE IF EXISTS menu_componente;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS cliente;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE cliente (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    identificador   VARCHAR(30)  NOT NULL,
    nombre          VARCHAR(100) NOT NULL,
    telefono        VARCHAR(20)  NULL,
    correo          VARCHAR(100) NULL,
    puntos          INT          NOT NULL DEFAULT 0,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_registro  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_cliente_identificador UNIQUE (identificador),
    CONSTRAINT ck_cliente_puntos CHECK (puntos >= 0)
) ENGINE = InnoDB;

CREATE TABLE producto (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(20)   NOT NULL,
    nombre          VARCHAR(100)  NOT NULL,
    categoria       ENUM ('SANDWICH', 'BEBIDA', 'ACOMPANAMIENTO', 'MENU') NOT NULL,
    precio          DECIMAL(10, 2) NOT NULL,
    existencia      INT           NOT NULL DEFAULT 0,
    activo          BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_registro  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_producto_codigo UNIQUE (codigo),
    CONSTRAINT ck_producto_precio CHECK (precio >= 0),
    CONSTRAINT ck_producto_existencia CHECK (existencia >= 0)
) ENGINE = InnoDB;

CREATE TABLE menu_componente (
    menu_id            INT PRIMARY KEY,
    sandwich_id        INT NOT NULL,
    bebida_id          INT NOT NULL,
    acompanamiento_id  INT NOT NULL,
    CONSTRAINT fk_menu_producto FOREIGN KEY (menu_id) REFERENCES producto (id),
    CONSTRAINT fk_menu_sandwich FOREIGN KEY (sandwich_id) REFERENCES producto (id),
    CONSTRAINT fk_menu_bebida FOREIGN KEY (bebida_id) REFERENCES producto (id),
    CONSTRAINT fk_menu_acompanamiento FOREIGN KEY (acompanamiento_id) REFERENCES producto (id)
) ENGINE = InnoDB;

CREATE TABLE movimiento_inventario (
    id                     INT AUTO_INCREMENT PRIMARY KEY,
    producto_id            INT          NOT NULL,
    tipo                   ENUM ('ENTRADA', 'VENTA', 'CANJE') NOT NULL,
    cantidad               INT          NOT NULL,
    existencia_resultante  INT          NOT NULL,
    referencia             VARCHAR(150) NULL,
    fecha                  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movinv_producto FOREIGN KEY (producto_id) REFERENCES producto (id),
    CONSTRAINT ck_movinv_cantidad CHECK (cantidad > 0),
    INDEX ix_movinv_fecha (fecha)
) ENGINE = InnoDB;

CREATE TABLE pedido (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id        INT            NULL,
    estado            ENUM ('PENDIENTE', 'PAGADO', 'ENTREGADO', 'ANULADO') NOT NULL DEFAULT 'PENDIENTE',
    total             DECIMAL(10, 2) NOT NULL,
    puntos_otorgados  INT            NOT NULL DEFAULT 0,
    fecha_creacion    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_pago        DATETIME       NULL,
    fecha_entrega     DATETIME       NULL,
    fecha_anulacion   DATETIME       NULL,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id),
    CONSTRAINT ck_pedido_total CHECK (total >= 0),
    INDEX ix_pedido_estado (estado),
    INDEX ix_pedido_fecha_pago (fecha_pago)
) ENGINE = InnoDB;

CREATE TABLE detalle_pedido (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id        INT            NOT NULL,
    producto_id      INT            NOT NULL,
    cantidad         INT            NOT NULL,
    precio_unitario  DECIMAL(10, 2) NOT NULL,
    subtotal         DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES producto (id),
    CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0)
) ENGINE = InnoDB;

CREATE TABLE pago (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id        INT            NOT NULL,
    metodo           ENUM ('EFECTIVO', 'TARJETA') NOT NULL,
    monto            DECIMAL(10, 2) NOT NULL,
    monto_recibido   DECIMAL(10, 2) NULL,
    cambio           DECIMAL(10, 2) NULL,
    referencia       VARCHAR(40)    NULL,
    ultimos_digitos  CHAR(4)        NULL,
    titular          VARCHAR(100)   NULL,
    fecha            DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_pago_pedido UNIQUE (pedido_id),
    CONSTRAINT fk_pago_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id),
    INDEX ix_pago_fecha (fecha)
) ENGINE = InnoDB;

CREATE TABLE recompensa (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    nombre             VARCHAR(100) NOT NULL,
    descripcion        VARCHAR(255) NULL,
    puntos_requeridos  INT          NOT NULL,
    producto_id        INT          NOT NULL,
    activa             BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_recompensa_producto FOREIGN KEY (producto_id) REFERENCES producto (id),
    CONSTRAINT ck_recompensa_puntos CHECK (puntos_requeridos > 0)
) ENGINE = InnoDB;

CREATE TABLE canje (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id         INT      NOT NULL,
    recompensa_id      INT      NOT NULL,
    producto_id        INT      NOT NULL,
    puntos_utilizados  INT      NOT NULL,
    fecha              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_canje_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id),
    CONSTRAINT fk_canje_recompensa FOREIGN KEY (recompensa_id) REFERENCES recompensa (id),
    CONSTRAINT fk_canje_producto FOREIGN KEY (producto_id) REFERENCES producto (id),
    CONSTRAINT ck_canje_puntos CHECK (puntos_utilizados > 0)
) ENGINE = InnoDB;

CREATE TABLE movimiento_puntos (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id        INT          NOT NULL,
    tipo              ENUM ('ACUMULACION', 'CANJE') NOT NULL,
    puntos            INT          NOT NULL,
    saldo_resultante  INT          NOT NULL,
    pedido_id         INT          NULL,
    canje_id          INT          NULL,
    descripcion       VARCHAR(150) NULL,
    fecha             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movpts_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id),
    CONSTRAINT fk_movpts_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id),
    CONSTRAINT fk_movpts_canje FOREIGN KEY (canje_id) REFERENCES canje (id),
    CONSTRAINT ck_movpts_puntos CHECK (puntos > 0),
    CONSTRAINT ck_movpts_saldo CHECK (saldo_resultante >= 0)
) ENGINE = InnoDB;

INSERT INTO cliente (id, identificador, nombre, telefono, correo, puntos, activo, fecha_registro) VALUES
    (1, 'CLI-1001', 'Juan Pérez',     '5555-1001', 'juan.perez@correo.com',   2,  TRUE,  '2026-10-01 09:00:00'),
    (2, 'CLI-1002', 'Ana López',      '5555-1002', 'ana.lopez@correo.com',    56, TRUE,  '2026-10-02 10:30:00'),
    (3, 'CLI-1003', 'Carlos Méndez',  '5555-1003', 'carlos.mendez@correo.com', 0, TRUE,  '2026-10-05 15:45:00'),
    (4, 'CLI-1004', 'Lucía Ramírez',  '5555-1004', NULL,                       0, FALSE, '2026-10-07 11:20:00');

INSERT INTO producto (id, codigo, nombre, categoria, precio, existencia, activo, fecha_registro) VALUES
    (1,  'SAN-001', 'Sándwich Club',          'SANDWICH',       35.00, 27, TRUE, '2026-10-01 08:00:00'),
    (2,  'SAN-002', 'Sándwich de Pollo',      'SANDWICH',       32.00, 24, TRUE, '2026-10-01 08:00:00'),
    (3,  'SAN-003', 'Sándwich Vegetariano',   'SANDWICH',       28.00,  5, TRUE, '2026-10-01 08:00:00'),
    (4,  'BEB-001', 'Gaseosa 355 ml',         'BEBIDA',         10.00, 48, TRUE, '2026-10-01 08:00:00'),
    (5,  'BEB-002', 'Limonada Natural',       'BEBIDA',         12.00, 18, TRUE, '2026-10-01 08:00:00'),
    (6,  'BEB-003', 'Agua Pura 500 ml',       'BEBIDA',          8.00,  0, TRUE, '2026-10-01 08:00:00'),
    (7,  'ACO-001', 'Papas Fritas',           'ACOMPANAMIENTO', 15.00, 39, TRUE, '2026-10-01 08:00:00'),
    (8,  'ACO-002', 'Aros de Cebolla',        'ACOMPANAMIENTO', 18.00,  4, TRUE, '2026-10-01 08:00:00'),
    (9,  'ACO-003', 'Ensalada de la Casa',    'ACOMPANAMIENTO', 16.00, 15, TRUE, '2026-10-01 08:00:00'),
    (10, 'MEN-001', 'Menú Club Clásico',      'MENU',           55.00,  0, TRUE, '2026-10-01 08:00:00'),
    (11, 'MEN-002', 'Menú Pollo Crujiente',   'MENU',           50.00,  0, TRUE, '2026-10-01 08:00:00'),
    (12, 'MEN-003', 'Menú Vegetariano',       'MENU',           45.00,  0, TRUE, '2026-10-01 08:00:00');

INSERT INTO menu_componente (menu_id, sandwich_id, bebida_id, acompanamiento_id) VALUES
    (10, 1, 4, 7),
    (11, 2, 5, 8),
    (12, 3, 6, 9);

INSERT INTO movimiento_inventario (producto_id, tipo, cantidad, existencia_resultante, referencia, fecha) VALUES
    (1, 'ENTRADA', 30, 30, 'Inventario inicial', '2026-10-01 08:00:00'),
    (2, 'ENTRADA', 31, 31, 'Inventario inicial', '2026-10-01 08:00:00'),
    (3, 'ENTRADA',  5,  5, 'Inventario inicial', '2026-10-01 08:00:00'),
    (4, 'ENTRADA', 50, 50, 'Inventario inicial', '2026-10-01 08:00:00'),
    (5, 'ENTRADA', 26, 26, 'Inventario inicial', '2026-10-01 08:00:00'),
    (7, 'ENTRADA', 40, 40, 'Inventario inicial', '2026-10-01 08:00:00'),
    (8, 'ENTRADA', 11, 11, 'Inventario inicial', '2026-10-01 08:00:00'),
    (9, 'ENTRADA', 15, 15, 'Inventario inicial', '2026-10-01 08:00:00'),
    (1, 'VENTA',    3, 27, 'Pedido #1',          '2026-10-10 12:15:00'),
    (4, 'VENTA',    1, 49, 'Pedido #1',          '2026-10-10 12:15:00'),
    (7, 'VENTA',    1, 39, 'Pedido #1',          '2026-10-10 12:15:00'),
    (2, 'VENTA',    7, 24, 'Pedido #2',          '2026-10-12 13:40:00'),
    (5, 'VENTA',    8, 18, 'Pedido #2',          '2026-10-12 13:40:00'),
    (8, 'VENTA',    7,  4, 'Pedido #2',          '2026-10-12 13:40:00'),
    (4, 'CANJE',    1, 48, 'Canje #1 - CLI-1001', '2026-10-15 16:00:00');

INSERT INTO pedido (id, cliente_id, estado, total, puntos_otorgados, fecha_creacion, fecha_pago, fecha_entrega) VALUES
    (1, 1, 'ENTREGADO', 125.00, 12, '2026-10-10 12:05:00', '2026-10-10 12:15:00', '2026-10-10 12:30:00'),
    (2, 2, 'PAGADO',    362.00, 56, '2026-10-12 13:30:00', '2026-10-12 13:40:00', NULL),
    (3, 3, 'PENDIENTE',  28.00,  0, '2026-10-20 09:10:00', NULL, NULL);

INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
    (1, 1,  2, 35.00,  70.00),
    (1, 10, 1, 55.00,  55.00),
    (2, 11, 7, 50.00, 350.00),
    (2, 5,  1, 12.00,  12.00),
    (3, 3,  1, 28.00,  28.00);

INSERT INTO pago (pedido_id, metodo, monto, monto_recibido, cambio, referencia, ultimos_digitos, titular, fecha) VALUES
    (1, 'EFECTIVO', 125.00, 150.00, 25.00, NULL, NULL, NULL, '2026-10-10 12:15:00'),
    (2, 'TARJETA',  362.00, NULL, NULL, 'TRX-20261012134000-4821', '1111', 'Ana López', '2026-10-12 13:40:00');

INSERT INTO recompensa (id, nombre, descripcion, puntos_requeridos, producto_id, activa) VALUES
    (1, 'Bebida gratuita',         'Una gaseosa 355 ml sin costo',            10, 4,  TRUE),
    (2, 'Acompañamiento gratuito', 'Una porción de papas fritas sin costo',   15, 7,  TRUE),
    (3, 'Sándwich gratuito',       'Un sándwich de pollo sin costo',          30, 2,  TRUE),
    (4, 'Menú gratuito',           'Un menú club clásico completo sin costo', 50, 10, TRUE);

INSERT INTO canje (id, cliente_id, recompensa_id, producto_id, puntos_utilizados, fecha) VALUES
    (1, 1, 1, 4, 10, '2026-10-15 16:00:00');

INSERT INTO movimiento_puntos (cliente_id, tipo, puntos, saldo_resultante, pedido_id, canje_id, descripcion, fecha) VALUES
    (1, 'ACUMULACION', 12, 12, 1, NULL, 'Puntos por pedido #1', '2026-10-10 12:15:00'),
    (2, 'ACUMULACION', 56, 56, 2, NULL, 'Puntos por pedido #2', '2026-10-12 13:40:00'),
    (1, 'CANJE',       10,  2, NULL, 1, 'Canje: Bebida gratuita', '2026-10-15 16:00:00');
