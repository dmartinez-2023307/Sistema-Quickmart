drop database if exists sistema_catalogo_in4am;
create database sistema_catalogo_in4am;
use sistema_catalogo_in4am;

CREATE TABLE Categoria (
    id_categoria INT AUTO_INCREMENT,
    nombre_categoria VARCHAR(50) NOT NULL,
    descripcion VARCHAR(150),
    CONSTRAINT PK_Categoria PRIMARY KEY (id_categoria),
    CONSTRAINT UQ_Categoria_Nombre UNIQUE (nombre_categoria)
);
CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT,
    correo VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    rol ENUM('Administrador', 'Bodeguero', 'Cajero') NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    CONSTRAINT PK_Usuario PRIMARY KEY (id_usuario),
    CONSTRAINT UQ_Usuario_Correo UNIQUE (correo)
);

CREATE TABLE Producto (
    id_producto INT AUTO_INCREMENT,
    codigo_barras VARCHAR(50) NOT NULL,
    nombre_comercial VARCHAR(100) NOT NULL,
    precio_costo DECIMAL(10, 2) NOT NULL,
    precio_venta DECIMAL(10, 2) NOT NULL,
    stock_actual INT NOT NULL DEFAULT 0,
    id_categoria INT,
    CONSTRAINT PK_Producto PRIMARY KEY (id_producto),
    CONSTRAINT UQ_Producto_CodigoBarras UNIQUE (codigo_barras),
    CONSTRAINT FK_Producto_Categoria FOREIGN KEY (id_categoria) 
        REFERENCES Categoria(id_categoria) ON DELETE SET NULL,
    CONSTRAINT CHK_Producto_Precio CHECK (precio_venta > precio_costo),
    CONSTRAINT CHK_Producto_Stock CHECK (stock_actual >= 0),
    CONSTRAINT CHK_Producto_Costo CHECK (precio_costo > 0)
);

CREATE TABLE Venta (
    id_venta INT AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    fecha_venta DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10, 2) NOT NULL,
    CONSTRAINT PK_Venta PRIMARY KEY (id_venta),
    CONSTRAINT FK_Venta_Usuario FOREIGN KEY (id_usuario) 
        REFERENCES Usuario(id_usuario)
);

CREATE TABLE detalle_venta (
    id_detalle INT AUTO_INCREMENT,
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT PK_DetalleVenta PRIMARY KEY (id_detalle),
    CONSTRAINT FK_DetalleVenta_Venta FOREIGN KEY (id_venta) 
        REFERENCES Venta(id_venta) ON DELETE CASCADE,
    CONSTRAINT FK_DetalleVenta_Producto FOREIGN KEY (id_producto) 
        REFERENCES Producto(id_producto)
);




-- STORED PROCEDURES: CATEGORIA

DELIMITER $$

CREATE PROCEDURE sp_crearCategoria(
    IN p_nombre VARCHAR(50),
    IN p_descripcion VARCHAR(150)
)
BEGIN
    INSERT INTO Categoria (nombre_categoria, descripcion )
    VALUES (p_nombre, p_descripcion);
    SELECT LAST_INSERT_ID() AS id_categoria;
END $$

CREATE PROCEDURE sp_listarCategorias()
BEGIN
    SELECT * FROM Categoria ORDER BY nombre_categoria;
END $$

CREATE PROCEDURE sp_buscarCategoriaPorId(IN p_id INT)
BEGIN
    SELECT * FROM Categoria WHERE id_categoria = p_id;
END $$

CREATE PROCEDURE sp_actualizarCategoria(
    IN p_id INT,
    IN p_nombre VARCHAR(50),
    IN p_descripcion VARCHAR(150)
)
BEGIN
    UPDATE Categoria
    SET nombre_categoria = p_nombre,
        descripcion = p_descripcion
    WHERE id_categoria = p_id;
END $$

CREATE PROCEDURE sp_eliminarCategoria(IN p_id INT)
BEGIN
    DELETE FROM Categoria WHERE id_categoria = p_id;
END $$

DELIMITER ;



-- STORED PROCEDURES: USUARIO

DELIMITER $$

CREATE PROCEDURE sp_crearUsuario(
    IN p_correo VARCHAR(100),
    IN p_password VARCHAR(255),
    IN p_nombre VARCHAR(100),
    IN p_rol VARCHAR(20)
)
BEGIN
    INSERT INTO Usuario (correo, password, nombre_completo, rol)
    VALUES (p_correo, p_password, p_nombre, p_rol);
    SELECT LAST_INSERT_ID() AS id_usuario;
END $$

CREATE PROCEDURE sp_listarUsuarios()
BEGIN
    SELECT id_usuario, correo, nombre_completo, rol, activo
    FROM Usuario
    WHERE activo = TRUE
    ORDER BY nombre_completo;
END $$

CREATE PROCEDURE sp_validarLogin(
    IN p_correo VARCHAR(100),
    IN p_password VARCHAR(255)
)
BEGIN
    SELECT id_usuario, correo, nombre_completo, rol
    FROM Usuario
    WHERE correo = p_correo
      AND password = p_password
      AND activo = TRUE;
END $$

CREATE PROCEDURE sp_editarUsuario(
    IN p_id INT,
    IN p_nombre VARCHAR(100),
    IN p_rol VARCHAR(20)
)
BEGIN
    UPDATE Usuario
    SET nombre_completo = p_nombre,
        rol = p_rol
    WHERE id_usuario = p_id;
END $$

-- DELETE: Soft delete (desactivar usuario)
CREATE PROCEDURE sp_eliminarUsuario(IN p_id INT)
BEGIN
    UPDATE Usuario SET activo = FALSE WHERE id_usuario = p_id;
END $$

DELIMITER ;

-- STORED PROCEDURES: PRODUCTO

DELIMITER $$

CREATE PROCEDURE sp_crearProducto(
    IN p_codigo_barras VARCHAR(50),
    IN p_nombre VARCHAR(100),
    IN p_precio_costo DECIMAL(10,2),
    IN p_precio_venta DECIMAL(10,2),
    IN p_stock INT,
    IN p_id_categoria INT
)
BEGIN
    INSERT INTO Producto (codigo_barras, nombre_comercial, precio_costo, precio_venta, stock_actual, id_categoria)
    VALUES (p_codigo_barras, p_nombre, p_precio_costo, p_precio_venta, p_stock, p_id_categoria);
    SELECT LAST_INSERT_ID() AS id_producto;
END $$

CREATE PROCEDURE sp_listarProductos()
BEGIN
    SELECT
        p.id_producto,
        p.codigo_barras,
        p.nombre_comercial,
        p.precio_costo,
        p.precio_venta,
        p.stock_actual,
        IFNULL(c.nombre_categoria, 'Sin categoría') AS nombre_categoria,
        (p.precio_venta - p.precio_costo) AS margen_ganancia
    FROM Producto p
    LEFT JOIN Categoria c ON p.id_categoria = c.id_categoria
    ORDER BY p.nombre_comercial;
END $$

CREATE PROCEDURE sp_buscarProducto(IN p_text VARCHAR(50) )
BEGIN
    SELECT
        p.id_producto,
        p.codigo_barras,
        p.nombre_comercial,
        p.precio_venta,
        p.stock_actual,
        c.nombre_categoria
    FROM Producto p
    LEFT JOIN Categoria c ON p.id_categoria = c.id_categoria
    WHERE p.codigo_barras like '%1001%'
		or p.codigo_barras like '%2001%'
        or p.codigo_barras like '%3001%'
        or p.codigo_barras like '%4001%'
        or p.codigo_barras like '%5001%' or c.categoria = p_text;
END $$


CREATE PROCEDURE sp_actualizarProducto(
    IN p_id INT,
    IN p_nombre VARCHAR(100),
    IN p_precio_costo DECIMAL(10,2),
    IN p_precio_venta DECIMAL(10,2),
    IN p_id_categoria INT
)
BEGIN
    UPDATE Producto
    SET nombre_comercial = p_nombre,
        precio_costo = p_precio_costo,
        precio_venta = p_precio_venta,
        id_categoria = p_id_categoria
    WHERE id_producto = p_id;
END $$

CREATE PROCEDURE sp_ajustarStock(
    IN p_id INT,
    IN p_cantidad INT
)
BEGIN
    UPDATE Producto
    SET stock_actual = stock_actual + p_cantidad
    WHERE id_producto = p_id;
END $$

CREATE PROCEDURE sp_eliminarProducto(IN p_id INT)
BEGIN
    DELETE FROM Producto WHERE id_producto = p_id;
END $$

DELIMITER ;




-- STORED PROCEDURES: VENTA

DELIMITER $$
CREATE PROCEDURE sp_crearVenta(
    IN p_id_usuario INT,
    IN p_total DECIMAL(10,2)
)
BEGIN
    INSERT INTO Venta (id_usuario, total)
    VALUES (p_id_usuario, p_total);
    
    SELECT LAST_INSERT_ID() AS id_venta;
END $$

CREATE PROCEDURE sp_agregarDetalle(
    IN p_id_venta INT,
    IN p_id_producto INT,
    IN p_cantidad INT,
    IN p_precio_unitario DECIMAL(10,2),
    IN p_subtotal DECIMAL(10,2)
)
BEGIN
    INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal)
    VALUES (p_id_venta, p_id_producto, p_cantidad, p_precio_unitario, p_subtotal);
END $$

CREATE PROCEDURE sp_descontarStock(
    IN p_id_producto INT,
    IN p_cantidad INT
)
BEGIN
    UPDATE Producto
    SET stock_actual = stock_actual - p_cantidad
    WHERE id_producto = p_id_producto;
END $$


CREATE PROCEDURE sp_listarVentas()
BEGIN
    SELECT
        v.id_venta,
        v.fecha_venta,
        u.nombre_completo AS cajero,
        v.total
    FROM Venta v
    INNER JOIN Usuario u ON v.id_usuario = u.id_usuario
    ORDER BY v.fecha_venta DESC;
END $$

CREATE PROCEDURE sp_detalleVenta(IN p_id_venta INT)
BEGIN
    SELECT
        p.nombre_comercial AS producto,
        dv.cantidad,
        dv.precio_unitario,
        dv.subtotal
    FROM detalle_venta dv
    INNER JOIN Producto p ON dv.id_producto = p.id_producto
    WHERE dv.id_venta = p_id_venta;
END $$

CREATE PROCEDURE sp_ventasPorCajero(IN p_id_usuario INT)
BEGIN
    SELECT
        COUNT(*) AS total_ventas,
        SUM(total) AS total_vendido,
        MIN(fecha_venta) AS primera_venta,
        MAX(fecha_venta) AS ultima_venta
    FROM Venta
    WHERE id_usuario = p_id_usuario;
END $$

CREATE PROCEDURE sp_ventasPorCategoria()
BEGIN
    SELECT
        c.nombre_categoria,
        SUM(dv.cantidad) AS unidades_vendidas,
        SUM(dv.subtotal) AS total_recaudado
    FROM detalle_venta dv
    INNER JOIN Producto p ON dv.id_producto = p.id_producto
    INNER JOIN Categoria c ON p.id_categoria = c.id_categoria
    GROUP BY c.nombre_categoria
    ORDER BY total_recaudado DESC;
END $$

DELIMITER ;
