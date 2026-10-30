# Qallet - Aplicación de Gestión de Finanzas Personales

## Información del Estudiante
- **Nombre:** Jefferson Osberto Salazar Escobar
- **Carnet:** 0905-25-7843
- **Variante:** A
- **Curso:** Programación II

---

## Guía de Instalación y Ejecución (APK)

La aplicación está empaquetada para su ejecución en dispositivos móviles Android. Para probarla:

1. Localiza el archivo ejecutable `.apk` dentro de la carpeta compartida en Google Drive (enlazada en el archivo `documentacion.txt`).
2. Descarga e instala el archivo `.apk` directamente en tu dispositivo Android o emulador (asegúrate de permitir la instalación de orígenes desconocidos si el sistema lo requiere).
3. Abre la aplicación **Qallet** instalada para interactuar con las pantallas de autenticación, historial y registro de movimientos.

---

## Justificación de Arquitectura y Estructura del Proyecto

El repositorio se encuentra dividido en dos módulos independientes para garantizar la separación de responsabilidades:

1. **`ControlGastos-core/` (Módulo Core / Consola):**
   - **Justificación:** Contiene la lógica de dominio pura y el entorno de pruebas inicial en consola (desarrollado en IntelliJ IDEA). Su propósito es separar las reglas de negocio de cualquier interfaz gráfica, permitiendo validar modelos, enums de categorías y repositorios en memoria de forma agnóstica a la plataforma.

2. **`app/` (Módulo Android):**
   - **Justificación:** Representa la capa de presentación y persistencia móvil. Consume la lógica de datos mediante una base de datos local robusta utilizando **Room** (`AppDatabase`, `TransaccionDao`, `TransaccionEntity`), corrutinas para operaciones asíncronas y un diseño visual basado en Material Design 3.
