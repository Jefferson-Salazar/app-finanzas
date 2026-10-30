# Documentación del Módulo Android (`app`)

##  Arquitectura y Componentes
Este módulo implementa la interfaz gráfica y la persistencia local de **Qallet** bajo los siguientes componentes técnicos:

- **Capa de Datos y Persistencia (`datos/`):**
  - `AppDatabase`: Configuración de la base de datos SQLite embebida mediante Room.
  - `TransaccionDao`: Interfaz con las consultas SQL optimizadas para las operaciones CRUD (crear, leer, actualizar, eliminar).
  - `TransaccionEntity`: Modelo de datos estructurado para el almacenamiento persistente.
- **Capa de Presentación (`ui/`):**
  - Actividades de flujo de usuario (`PinActivity`, `RegisterActivity`, `MainActivity`, `HistorialActivity`, `FormularioMovimientoActivity`).
  - Uso de adaptadores personalizados (`TransaccionAdapter`) con soporte para selección múltiple y eliminación en lote.
- **Concurrencia y Rendimiento:**
  - Empleo de corrutinas de Kotlin (`lifecycleScope`) para ejecutar consultas de base de datos en hilos secundarios, evitando bloqueos en el hilo principal de la interfaz de usuario.