CALL sp_crearCategoria('Lácteos', 'Leche, yogurt, quesos y derivados');
CALL sp_crearCategoria('Abarrotes', 'Productos de despensa y enlatados');
CALL sp_crearCategoria('Bebidas', 'Refrescos, jugos y agua');
CALL sp_crearCategoria('Limpieza', 'Productos de aseo personal y del hogar');
CALL sp_crearCategoria('Panadería', 'Pan, galletas y productos horneados');

CALL sp_crearUsuario('admin@gmail.com', '12345', 'Pablo Us', 'Administrador');


-- LÁCTEOS
CALL sp_crearProducto('1001962366', 'Leche Entera Lala 1L', 15.50, 22.00, 50, 1);
CALL sp_crearProducto('1001234567', 'Yogurt Danone Fresa 125g', 6.50, 12.00, 80, 1);
CALL sp_crearProducto('1001234568', 'Queso Oaxaca 500g', 45.00, 72.00, 30, 1);
CALL sp_crearProducto('1001234569', 'Mantequilla Lala 250g', 28.00, 42.00, 40, 1);
CALL sp_crearProducto('1001234570', 'Crema Nestlé 400ml', 22.00, 35.00, 50, 1);
CALL sp_crearProducto('1001234571', 'Leche Deslactosada 1L', 18.00, 28.00, 60, 1);
CALL sp_crearProducto('1001234572', 'Queso Fresco 400g', 30.00, 48.00, 40, 1);
CALL sp_crearProducto('1001234573', 'Leche en polvo 900g', 85.00, 135.00, 25, 1);
CALL sp_crearProducto('1001234574', 'Requesón 250g', 18.00, 30.00, 35, 1);
CALL sp_crearProducto('1001234575', 'Flan Napolitano 120g', 8.00, 14.00, 100, 1);

-- ABARROTES 
CALL sp_crearProducto('2001234566', 'Arroz Super 1kg', 18.00, 28.50, 75, 2);
CALL sp_crearProducto('2001234567', 'Frijoles Ducal 1kg', 22.00, 35.00, 100, 2);
CALL sp_crearProducto('2001234568', 'Pasta Spaghetti 500g', 12.00, 20.00, 120, 2);
CALL sp_crearProducto('2001234569', 'Aceite Vegetal 1L', 32.00, 48.00, 80, 2);
CALL sp_crearProducto('2001234570', 'Azúcar Morena 1kg', 16.00, 26.00, 90, 2);
CALL sp_crearProducto('2001234571', 'Sal Yodada 1kg', 8.00, 15.00, 150, 2);
CALL sp_crearProducto('2001234572', 'Harina de Trigo 1kg', 18.00, 28.00, 70, 2);
CALL sp_crearProducto('2001234573', 'Atún La Sirena 140g', 18.00, 28.00, 100, 2);
CALL sp_crearProducto('2001234574', 'Salsa Valentina 370ml', 15.00, 24.00, 85, 2);
CALL sp_crearProducto('2001234575', 'Café Nescafé 200g', 42.00, 65.00, 60, 2);
CALL sp_crearProducto('2009876541', 'Mayonesa McCormick 400g', 25.00, 40.00, 60, 2);
CALL sp_crearProducto('2009876542', 'Salsa De Tomate Kerns 350g', 28.00, 45.00, 55, 2);
CALL sp_crearProducto('2009876543', 'Sopa de pasta La Costeña', 8.00, 14.00, 200, 2);
CALL sp_crearProducto('2009876544', 'Cereal Zucaritas 400g', 45.00, 72.00, 40, 2);
CALL sp_crearProducto('2009876545', 'Chocolate en polvo 400g', 32.00, 52.00, 50, 2);

-- BEBIDAS 
CALL sp_crearProducto('3001234566', 'Coca-Cola 600ml', 10.00, 16.50, 100, 3);
CALL sp_crearProducto('3001234567', 'Agua Cielo 1L', 6.00, 11.00, 200, 3);
CALL sp_crearProducto('3001234568', 'Jugo del Valle 1L', 18.00, 28.00, 70, 3);
CALL sp_crearProducto('3001234569', 'Cerveza Corona 355ml', 14.00, 22.00, 150, 3);
CALL sp_crearProducto('3001234570', 'Pepsi 2L', 18.00, 28.00, 100, 3);
CALL sp_crearProducto('3001234571', 'Agua Mineral 600ml', 8.00, 14.00, 120, 3);
CALL sp_crearProducto('3001234572', 'Jugo Naranja Natural 1L', 22.00, 35.00, 50, 3);
CALL sp_crearProducto('3001234573', 'Cerveza Modelo 355ml', 13.00, 20.00, 150, 3);
CALL sp_crearProducto('3001234574', 'Té Helado Lipton 500ml', 12.00, 20.00, 100, 3);
CALL sp_crearProducto('3001234575', 'Monster 250ml', 18.00, 30.00, 80, 3);
CALL sp_crearProducto('3001234576', 'Chocolate Caliente 250ml', 10.00, 18.00, 90, 3);
CALL sp_crearProducto('3001234577', 'Agua Saborizada Melocoton 1L', 12.00, 20.00, 120, 3);
CALL sp_crearProducto('3001234578', 'Agua Saborizada Fresa 1L', 12.00, 20.00, 120, 3);
CALL sp_crearProducto('3001234579', 'Agua Saborizada Uva 1L', 12.00, 20.00, 120, 3);


-- LIMPIEZA 
CALL sp_crearProducto('4001234566', 'Jabón Protex 500g', 12.00, 21.00, 40, 4);
CALL sp_crearProducto('4001234567', 'Detergente Ariel 1kg', 38.00, 58.00, 60, 4);
CALL sp_crearProducto('4001234568', 'Cloro Magia Blanca 1L', 12.00, 20.00, 80, 4);
CALL sp_crearProducto('4001234569', 'Suavitel Downy 1L', 32.00, 48.00, 50, 4);
CALL sp_crearProducto('4001234570', 'Jabón Zote 400g', 18.00, 28.00, 100, 4);
CALL sp_crearProducto('4001234571', 'Desinfectante Pine-Sol 1L', 28.00, 42.00, 70, 4);
CALL sp_crearProducto('4001234572', 'Papel Baño Scott 4 rollos', 32.00, 52.00, 90, 4);
CALL sp_crearProducto('4001234573', 'Shampoo Head & Shoulders 400ml', 48.00, 72.00, 40, 4);
CALL sp_crearProducto('4001234574', 'Pasta Dental Colgate 150g', 22.00, 35.00, 80, 4);
CALL sp_crearProducto('4001234575', 'Jabón para trastes 1L', 18.00, 30.00, 100, 4);
CALL sp_crearProducto('4001234576', 'Escoba de plástico', 25.00, 45.00, 30, 4);
CALL sp_crearProducto('4001234577', 'Esponja multiusos 3 pzs', 12.00, 22.00, 150, 4);
CALL sp_crearProducto('4001234578', 'Bolsas de basura 10 pzs', 15.00, 28.00, 80, 4);
CALL sp_crearProducto('4001234579', 'Papel Baño Scott 12 rollos', 40.00, 60.00, 90, 4);
CALL sp_crearProducto('4001234580', 'Papel Baño Scott 24 rollos', 45.00, 65.00, 90, 4);



-- PANADERÍA
CALL sp_crearProducto('5001234566', 'Pan Bimbo Grande', 35.00, 52.00, 20, 5);
CALL sp_crearProducto('5001234567', 'Galletas Marías 400g', 18.00, 28.00, 100, 5);
CALL sp_crearProducto('5001234568', 'Tortillas de Harina 1kg', 22.00, 35.00, 60, 5);
CALL sp_crearProducto('5001234569', 'Bollos Bimbo 6 piezas', 28.00, 42.00, 50, 5);
CALL sp_crearProducto('5001234570', 'Croissants 4 piezas', 32.00, 48.00, 40, 5);
CALL sp_crearProducto('5001234571', 'Donas Glaseadas 6 piezas', 35.00, 52.00, 35, 5);
CALL sp_crearProducto('5001234572', 'Pan de caja integral', 38.00, 58.00, 40, 5);
CALL sp_crearProducto('5001234573', 'Donas de chocolate 4 pzs', 25.00, 40.00, 45, 5);
CALL sp_crearProducto('5001234574', 'Galletas de chocolate 200g', 18.00, 30.00, 90, 5);
CALL sp_crearProducto('5001234575', 'Mantecadas 6 piezas', 22.00, 36.00, 50, 5);




call sp_listarUsuarios();
call sp_listarCategorias();
call sp_listarProductos();