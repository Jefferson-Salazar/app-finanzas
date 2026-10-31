package edu.umg.programacion2.appfinanzas.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.umg.programacion2.appfinanzas.Dependencias
import edu.umg.programacion2.appfinanzas.R
import edu.umg.programacion2.appfinanzas.modelo.Transaccion
import kotlinx.coroutines.launch

// Actividad encargada de mostrar el formulario para registrar o editar un ingreso o gasto
class FormularioMovimientoActivity : AppCompatActivity() {

    // Instancia del repositorio para guardar o actualizar los datos
    private val repositorio = Dependencias.repositorio

    private lateinit var tvTituloFormulario: TextView
    private lateinit var spinnerCategoria: Spinner
    private lateinit var etMonto: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnRegresar: ImageButton

    private var esModoEdicion = false
    private var transaccionId: Int = 0
    private var tipoMovimiento: String = "INGRESO"
    private var fechaOriginal: String = ""

    // Listas predefinidas de categorías para ingresos y gastos
    private val categoriasIngresos = listOf("Salario", "Ventas", "Inversiones", "Regalos", "Otros")
    private val categoriasGastos = listOf("Comida", "Transporte", "Servicios", "Entretenimiento", "Salud", "Educación", "Otros")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_movimiento)

        // Conecta las variables del código con los elementos visuales del archivo XML
        tvTituloFormulario = findViewById(R.id.tvTituloFormulario)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        etMonto = findViewById(R.id.etMonto)
        etDescripcion = findViewById(R.id.etDescripcion)
        btnGuardar = findViewById(R.id.btnGuardarMovimiento)
        btnRegresar = findViewById(R.id.btnRegresarFormulario)

        // Recibe los datos enviados desde la pantalla anterior mediante el Intent
        tipoMovimiento = intent.getStringExtra("TIPO_MOVIMIENTO") ?: "INGRESO"
        esModoEdicion = intent.getBooleanExtra("EDITAR_MODO", false)

        // Configura el menú desplegable (Spinner) con la lista de categorías correspondiente
        val listaCategorias = if (tipoMovimiento.equals("INGRESO", true)) categoriasIngresos else categoriasGastos
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaCategorias)
        spinnerCategoria.adapter = adapterSpinner

        // Si estamos editando, rellena los campos con los datos existentes de la transacción
        if (esModoEdicion) {
            transaccionId = intent.getIntExtra("TRANSACCION_ID", 0)
            fechaOriginal = intent.getStringExtra("FECHA") ?: System.currentTimeMillis().toString()

            val categoriaRecibida = intent.getStringExtra("CATEGORIA") ?: ""
            val montoRecibido = intent.getDoubleExtra("MONTO", 0.0)
            val descripcionRecibida = intent.getStringExtra("DESCRIPCION") ?: ""

            // Selecciona automáticamente la categoría correcta en el Spinner
            val indexCategoria = listaCategorias.indexOf(categoriaRecibida)
            if (indexCategoria >= 0) {
                spinnerCategoria.setSelection(indexCategoria)
            }

            etMonto.setText(if (montoRecibido > 0.0) montoRecibido.toString() else "")
            etDescripcion.setText(descripcionRecibida)

            tvTituloFormulario.text = if (tipoMovimiento.equals("INGRESO", true)) "Editar Ingreso" else "Editar Gasto"
            btnGuardar.text = "Actualizar Cambios"
        } else {
            tvTituloFormulario.text = if (tipoMovimiento.equals("INGRESO", true)) "Nuevo Ingreso" else "Nuevo Gasto"
            btnGuardar.text = "Guardar Movimiento"
            fechaOriginal = System.currentTimeMillis().toString()
        }

        // Cierra la pantalla actual al presionar el botón de regresar
        btnRegresar.setOnClickListener { finish() }

        // Ejecuta la función de guardar o actualizar al hacer clic en el botón principal
        btnGuardar.setOnClickListener {
            guardarOActualizarTransaccion(listaCategorias)
        }
    }

    // Valida los datos y los envía al repositorio (para guardar en la BD local)
    private fun guardarOActualizarTransaccion(listaCategorias: List<String>) {
        val categoria = spinnerCategoria.selectedItem?.toString() ?: listaCategorias.first()
        val montoStr = etMonto.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()

        // Validación básica de campo vacío
        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa el monto", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoStr.toDoubleOrNull() ?: 0.0

        // Lanza una corrutina en segundo plano para interactuar con la base de datos sin congelar la app
        lifecycleScope.launch {
            try {
                val transaccion = Transaccion(
                    id = if (esModoEdicion) transaccionId else 0,
                    usuarioId = 1,
                    tipo = tipoMovimiento,
                    categoria = categoria,
                    monto = monto,
                    descripcion = descripcion,
                    fecha = fechaOriginal
                )

                if (esModoEdicion) {
                    repositorio.actualizar(transaccion)
                    Toast.makeText(this@FormularioMovimientoActivity, "Transacción actualizada", Toast.LENGTH_SHORT).show()
                } else {
                    repositorio.agregar(transaccion)
                    Toast.makeText(this@FormularioMovimientoActivity, "Transacción guardada", Toast.LENGTH_SHORT).show()
                }
                finish() // Cierra la pantalla y regresa al Dashboard
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@FormularioMovimientoActivity, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}