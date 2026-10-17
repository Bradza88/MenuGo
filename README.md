# MenuGo
Aplicación Android para consultar productos, gestionar pedidos y localizar sucursales de una cadena de comida rápida

1. Descripción de las pantallas

Pantalla N.01: Inicio
Propósito: Punto de entrada del cliente. Muestra la marca, la sucursal predeterminada y la oferta destacada.
Acciones disponibles: Buscar productos, entrar a una categoría (Hamburguesas, Bebidas, Postres), ver la promoción del día (Combo Doble Queso, CRC 4.200), cambiar la sucursal predeterminada (MenuGo Centro), abrir el modo Admin con el botón superior.

Pantalla N.02: Menú (Catálogo de productos)
Propósito: Lista de 12 productos con imagen, descripción corta y precio.
Acciones disponibles: Buscar por nombre, abrir Filtros (0), pulsar Ver producto en cada tarjeta

Pantalla N.03: Filtros
Propósito:Acotar el catálogo.
Acciones disponibles: Elegir categoría (botones de opción), definir precio mínimo y máximo, activar “Solo promociones” (casilla), Aplicar Filtros, Limpiar Filtros, Cancelar o cerrar con [X].

Pantalla N.04: Detalle de producto
Propósito: Mostrar la Hamburguesa Clásica (CRC 3.000) con descripción, categoría y nota de pago.
Acciones disponibles: Ajustar cantidad con − / +, ver el subtotal (2 uds. = CRC 6.000), Agregar al Carrito, cerrar con X.

Pantalla N.05: Carrito
Propósito: Revisar el pedido antes de confirmarlo.
Acciones disponibles: Cambiar cantidad, [Quitar], Elegir sucursal, ver el desglose (subtotal, pago al retirar, modalidad, total) y pulsar Revisar Pedido.

Pantalla N.06: Sucursales
Propósito: Consultar y elegir dónde retirar.
Acciones disponibles: Ver el mapa ilustrativo con marcadores A y B, ver dirección y horario de cada sede, Seleccionar Sucursal. Centro aparece como “Activa”.

Pantalla N.07: Panel de administración
Propósito: Gestión del catálogo (modo admin).
Acciones disponibles: Buscar en el inventario, + Agregar, Editar y Eliminar por producto, Volver a App Cliente.

Pantalla N.08: Agregar / editar producto
Propósito: Formulario de alta y edición.
Acciones disponibles: Tomar foto, Abrir galería, completar nombre, descripción, precio y categoría, Guardar Cambios, Cancelar.

Pantalla N.09: Confirmar pedido
Propósito: Último paso antes de enviar el pedido.
Acciones disponibles: Revisar productos, monto, sucursal y modalidad, leer el aviso de pago al retirar, Confirmar Pedido o Volver al Carrito.

Pantalla N.10: Eliminar producto
Propósito: Confirmar el borrado de un producto (Hamburguesa BBQ Bacon, CRC 3.800).
Acciones disponibles: Leer la advertencia de irreversibilidad, Eliminar Producto o Cancelar y Volver.

2. Cumplimiento de funcionalidades

Funcionalidad: CRUD: Crear
Pantallas que la cubren: 07 (+ Agregar) → 08
Observaciones: Formulario vacío para producto nuevo.

Funcionalidad: CRUD: Leer
Pantallas que la cubren: 02 y 07 (listados), 04 (detalle)
Observaciones: Cliente y administrador tienen su propia vista.

Funcionalidad: CRUD: Actualizar
Pantallas que la cubren: 07 (Editar) → 08
Observaciones: La pantalla 08 sirve para crear y editar, y se muestra con datos de la Hamburguesa Clásica.

Funcionalidad: CRUD: Eliminar
Pantallas que la cubren: 07 (Eliminar) → 10
Observaciones: Pasa siempre por diálogo de confirmación.

Funcionalidad: Filtros
Pantallas que la cubren: 02 (botón Filtros) y 03
Observaciones: Categoría, rango de precio y solo promociones.

Funcionalidad: Cámara o galería
Pantallas que la cubren: 08
Observaciones: Botones Tomar foto y Abrir galería.		

Funcionalidad: Dialog
Pantallas que la cubren: 09 y 10
Observaciones: Ambos son modales sobre contenido atenuado.

Funcionalidad: Mapa
Pantallas que la cubren: 06
Observaciones: Mapa ilustrativo con marcadores A y B.

3. Decisiones de UX

Navegación
-Barra inferior fija con cuatro destinos: [Inicio], [Menú], [Carrito], [Sucursales]. La pestaña activa se marca con subrayado y negrita.
-Cada pantalla lleva una etiqueta numerada en la franja oscura inferior (01 INICIO, 02 MENÚ…), útil para documentar el flujo.
-Las pantallas secundarias (03, 04, 08) se cierran con [X]. Los diálogos ofrecen una salida explícita.
-El modo administrador se distingue con el rótulo “MODO ADMIN” en la barra de estado y una ruta de regreso (“Volver a App Cliente”).
-La sucursal predeterminada es visible desde Inicio y Carrito, con acceso directo para cambiarla.

Etiquetas y mensajes
-Los botones usan verbos claros: Aplicar, Limpiar, Agregar al Carrito, Revisar Pedido, Confirmar Pedido.
-La jerarquía visual distingue la acción principal (fondo oscuro) de la secundaria (gris).
-El precio total aparece en el botón “Agregar al Carrito (CRC 6.000)” y en “Total a Pagar en Caja”.
-La modalidad de pago se repite en tres puntos: nota en el detalle (04), resumen del carrito (05) y aviso del diálogo (09).

Validación de campos
-En el formulario 08, los campos obligatorios llevan asterisco () y la leyenda “ Campos obligatorios” en el pie.
-Los diseños no muestran estados de error ni reglas de validación.

Confirmaciones
-Enviar pedido: el diálogo 09 resume el pedido y avisa que el tiempo de retiro se confirma en caja.
-Eliminar: el diálogo 10 muestra el nombre y precio del producto, advierte que la acción es irreversible y afecta a todas las sucursales, y ofrece cancelar.

4. Mejoras pendientes

Categorías: Inicio y Menú usan “Postres”, el filtro usa “Acompañamientos y Postres”, y el panel de administración usa “Acompañamientos”. Conviene unificar la lista.
Descripción del producto: la pantalla 04 describe la Hamburguesa Clásica con queso cheddar, tomate y pan artesanal con ajonjolí. La pantalla 08 dice “tomate y salsa especial”, y el catálogo dice “Carne 150g, queso, lechuga”. Debería ser un único dato.
Filtros: el rango de precio y la casilla “Solo promociones” aparecen ya completados (CRC 1000–6000, marcada) mientras el botón del catálogo dice Filtros (0). El contador debería reflejar los filtros activos.
Estado de los listados: el catálogo dice “12 productos” pero solo muestra 4, y el carrito dice “1 ítem (2 uds.)”. Basta con aclarar si hay desplazamiento o paginación.
Sucursales: “Sucursales Disponibles” muestra “2 sedes” con datos de ejemplo, mientras el carrito ya da la dirección real de Centro. Hay que reemplazar los textos de ejemplo.
Formato de moneda: “CRC 1000” y “3000” (sin separador) frente a “CRC 3.000”. Conviene usar el mismo formato en todo el diseño.


Aplicación Android para consultar el menú, gestionar pedidos para retiro y localizar sucursales de una cadena de comida rápida.

## Integrantes

- Bryan Matamoros — GitHub: @Bradza88
- Kristal García — GitHub: @Krily0Angel

## Idea del proyecto

MenuGo permitirá consultar productos de una sola cadena de comida rápida, buscar y filtrar el menú, revisar los detalles de cada producto y agregarlos a un carrito. El cliente podrá seleccionar una sucursal en un mapa y confirmar su pedido para retirarlo.

La aplicación también contará con un área de administración para crear, consultar, editar y eliminar productos del menú, incluyendo sus imágenes.

## Objetivo

Facilitar la consulta del menú y la preparación de pedidos para retiro, y permitir la administración del catálogo de productos.

## Usuarios

- **Cliente:** consulta el menú, filtra productos, utiliza el carrito y selecciona una sucursal.
- **Administrador:** administra los productos del menú y sus imágenes.

## Funcionalidades previstas

### CRUD de productos

El administrador podrá crear, consultar, actualizar y eliminar productos. Cada producto tendrá nombre, descripción, precio, categoría e imagen. La eliminación solicitará confirmación.

### Lista con filtros personalizados

El cliente podrá buscar productos por nombre y combinar filtros por categoría, rango de precio y productos en promoción. También podrá limpiar los filtros aplicados.

### Cámara o galería

Al crear o editar un producto, el administrador podrá tomar una fotografía con la cámara o seleccionar una imagen de la galería.

### Diálogos

Se utilizarán diálogos para confirmar pedidos y eliminaciones de productos. La aplicación mostrará mensajes claros cuando falten datos o una operación no pueda completarse.

### Mapa de sucursales

El cliente podrá visualizar las sucursales en un mapa, consultar su nombre y dirección y seleccionar dónde retirará el pedido.

### Carrito

El cliente podrá revisar los productos seleccionados, modificar cantidades, quitar productos y consultar el total antes de confirmar.

## Alcance inicial

El proyecto contempla el catálogo de productos, su administración, el carrito y la selección de una sucursal para retiro. No incluye pagos en línea ni entregas a domicilio.

## Tecnologías previstas

- Lenguaje: Kotlin.
- Entorno de desarrollo: Android Studio.
- Control de versiones: Git y GitHub.

## Pantallas previstas

1. Inicio con categorías y acceso al menú.
2. Lista de productos con búsqueda y filtros.
3. Detalle de producto.
4. Carrito y resumen del pedido.
5. Mapa y selección de sucursal.
6. Administración de productos.
7. Formulario para agregar o editar un producto.
8. Diálogos de confirmación de pedido y eliminación.

## Criterios de experiencia de usuario

- Navegación consistente y botones con nombres claros.
- Precios y total visibles antes de confirmar el pedido.
- Campos identificados y mensajes de validación comprensibles.

- Confirmación antes de eliminar productos o enviar pedidos.
- Textos legibles, buen contraste y controles fáciles de presionar.
- Mensajes para listas vacías, búsquedas sin resultados y permisos de cámara denegados.

- ## Mockups de la aplicación

Los diseños muestran las diez pantallas previstas para MenuGo.
Los productos, las sucursales y el mapa contienen datos ilustrativos.
### 01 Inicio
![Inicio](<mockups/Captura de pantalla 2026-10-15 172635.png>)

### 02 Menú
![Menú](<mockups/Captura de pantalla 2026-10-15 172644.png>)

### 03 Filtros
![Filtros](<mockups/Captura de pantalla 2026-10-15 172839.png>)

### 04 Detalle de producto
![Detalle de producto](<mockups/Captura de pantalla 2026-10-15 173317.png>)

### 05 Carrito
![Carrito](<mockups/Captura de pantalla 2026-10-15 173327.png>)

### 06 Sucursales
![Sucursales](<mockups/Captura de pantalla 2026-10-15 173354.png>)

### 07 Administración
![Administración](<mockups/Captura de pantalla 2026-10-15 173407.png>)

### 08 Agregar o editar producto
![Formulario de producto](<mockups/Captura de pantalla 2026-10-15 173422.png>)

### 09 Confirmación del pedido
![Confirmación del pedido](<mockups/Captura de pantalla 2026-10-15 173448.png>)

### 10 Confirmación de eliminación
![Confirmación de eliminación](<mockups/Captura de pantalla 2026-10-15 173506.png>)
