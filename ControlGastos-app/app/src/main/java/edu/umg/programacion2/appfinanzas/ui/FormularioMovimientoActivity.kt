package edu.umg.programacion2.appfinanzas.ui

import android.os.Bundle
import android.view.View
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

class FormularioMovimientoActivity : AppCompatActivity() {

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

    // Listas predefinidas de categorías
    private val categoriasIngresos = listOf("Salario", "Ventas", "Inversiones", "Regalos", "Otros")
    private val categoriasGastos = listOf("Comida", "Transporte", "Servicios", "Entretenimiento", "Salud", "Educación", "Otros")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_movimiento)

        tvTituloFormulario = findViewById(R.id.tvTituloFormulario)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        etMonto = findViewById(R.id.etMonto)
        etDescripcion = findViewById(R.id.etDescripcion)
        btnGuardar = findViewById(R.id.btnGuardarMovimiento)
        btnRegresar = findViewById(R.id.btnRegresarFormulario)

        // Recibir datos del Intent
        tipoMovimiento = intent.getStringExtra("TIPO_MOVIMIENTO") ?: "INGRESO"
        esModoEdicion = intent.getBooleanExtra("EDITAR_MODO", false)

        // Configurar el Spinner con la lista correspondiente según el tipo
        // Configurar el Spinner con un diseño limpio y texto oscuro visible
        val listaCategorias = if (tipoMovimiento.equals("INGRESO", true)) categoriasIngresos else categoriasGastos
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaCategorias)
        spinnerCategoria.adapter = adapterSpinner

        if (esModoEdicion) {
            transaccionId = intent.getIntExtra("TRANSACCION_ID", 0)
            fechaOriginal = intent.getStringExtra("FECHA") ?: System.currentTimeMillis().toString()

            val categoriaRecibida = intent.getStringExtra("CATEGORIA") ?: ""
            val montoRecibido = intent.getDoubleExtra("MONTO", 0.0)
            val descripcionRecibida = intent.getStringExtra("DESCRIPCION") ?: ""

            // Seleccionar automáticamente la categoría en el Spinner
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

        btnRegresar.setOnClickListener { finish() }

        btnGuardar.setOnClickListener {
            guardarOActualizarTransaccion(listaCategorias)
        }
    }

    private fun guardarOActualizarTransaccion(listaCategorias: List<String>) {
        val categoria = spinnerCategoria.selectedItem?.toString() ?: listaCategorias.first()
        val montoStr = etMonto.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()

        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa el monto", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoStr.toDoubleOrNull() ?: 0.0

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
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@FormularioMovimientoActivity, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}