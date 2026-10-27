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
import kotlinx.coroutines.launch

class FormularioMovimientoActivity : AppCompatActivity() {

    private val repositorio = Dependencias.repositorio

    private lateinit var tvTituloFormulario: TextView
    private lateinit var spinnerCategoria: Spinner
    private lateinit var etConcepto: EditText
    private lateinit var etMonto: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnRegresar: ImageButton

    private var tipoMovimientoActual = "INGRESO"

    private val categoriasIngresos = listOf("Salario", "Ventas", "Transferencias", "Otros")
    private val categoriasGastos = listOf("Alimentación", "Transporte", "Universidad", "Servicios", "Entretenimiento", "Otros")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_movimiento)

        tvTituloFormulario = findViewById(R.id.tvTituloFormulario)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        etConcepto = findViewById(R.id.etConcepto)
        etMonto = findViewById(R.id.etMonto)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnRegresar = findViewById(R.id.btnRegresar)

        // Recibir el tipo de movimiento enviado desde el botón principal
        tipoMovimientoActual = intent.getStringExtra("TIPO_MOVIMIENTO") ?: "INGRESO"

        // Configurar título y categorías de forma dinámica
        if (tipoMovimientoActual.equals("INGRESO", ignoreCase = true)) {
            tvTituloFormulario.text = "Nuevo Ingreso"
            actualizarSpinner(categoriasIngresos)
        } else {
            tvTituloFormulario.text = "Nuevo Egreso"
            actualizarSpinner(categoriasGastos)
        }

        // Botón de retroceso f
        btnRegresar.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            guardarMovimiento()
        }
    }

    private fun actualizarSpinner(categorias: List<String>) {

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapter
    }

    private fun guardarMovimiento() {
        val montoStr = etMonto.text.toString()
        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoStr.toDoubleOrNull() ?: 0.0
        val categoria = spinnerCategoria.selectedItem.toString()
        val descripcion = etConcepto.text.toString()

        val conceptoFinal = if (descripcion.isBlank()) categoria else descripcion
        val fechaSistema = System.currentTimeMillis().toString()
        val usuarioIdFijo = 1

        val nuevaTransaccion = edu.umg.programacion2.appfinanzas.modelo.Transaccion(
            id = 0,
            usuarioId = usuarioIdFijo,
            tipo = tipoMovimientoActual,
            monto = monto,
            categoria = categoria,
            descripcion = conceptoFinal,
            fecha = fechaSistema
        )

        lifecycleScope.launch {
            try {
                repositorio.agregar(nuevaTransaccion)
                Toast.makeText(this@FormularioMovimientoActivity, "¡Movimiento guardado con éxito!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@FormularioMovimientoActivity, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}