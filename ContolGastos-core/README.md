# Documentación del Módulo Core (`ControlGastos-core`)

##  Justificación Técnica y Diseño
Este directorio contiene el código fuente desarrollado en fases tempranas del curso ejecutado en consola.

- **Desacoplamiento Estricto:** La lógica encargada de registrar, listar y calcular flujos financieros se diseñó de manera independiente al entorno Android. Esto cumple con el principio de responsabilidad única, asegurando que las reglas del negocio no dependan de componentes gráficos de vistas.
- **Validación del Dominio:** Permitió verificar la integridad de los datos, el manejo de excepciones con null-safety y las estructuras de repositorios en memoria antes de escalar el desarrollo hacia la arquitectura móvil con Room.