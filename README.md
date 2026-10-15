# MenuGo

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
