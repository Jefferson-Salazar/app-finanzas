package edu.umg.programacion2.appfinanzas.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.umg.programacion2.appfinanzas.Dependencias
import edu.umg.programacion2.appfinanzas.R
import kotlinx.coroutines.launch

class FormularioMovimientoActivity : AppCompatActivity() {

    private val repositorio = Dependencias.repositorio

    private lateinit var rgTipo: RadioGroup
    private lateinit var rbIngreso: RadioButton
    private lateinit var spinnerCategoria: Spinner
    private lateinit var etConcepto: EditText
    private lateinit var etMonto: EditText
    private lateinit var btnGuardar: Button

    private val categoriasIngresos = listOf("Salario", "Ventas", "Transferencias", "Otros")
    private val categoriasGastos = listOf("Alimentación", "Transporte", "Universidad", "Servicios", "Entretenimiento", "Otros")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_movimiento)

        rgTipo = findViewById(R.id.rgTipo)
        rbIngreso = findViewById(R.id.rbIngreso)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        etConcepto = findViewById(R.id.etConcepto)
        etMonto = findViewById(R.id.etMonto)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Configurar categorías iniciales
        actualizarSpinner(categoriasIngresos)

        // Cambiar las categorías
        rgTipo.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbIngreso) {
                actualizarSpinner(categoriasIngresos)
            } else {
                actualizarSpinner(categoriasGastos)
            }
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
        val tipo = if (rbIngreso.isChecked) "INGRESO" else "GASTO"
        val categoria = spinnerCategoria.selectedItem.toString()
        val descripcion = etConcepto.text.toString()

        // Concepto final combinando categoría y descripción opcional
        val conceptoFinal = if (descripcion.isBlank()) categoria else "$descripcion"

        // Fecha automática del sistema en milisegundos convertida a String
        val fechaSistema = System.currentTimeMillis().toString()
        val usuarioIdFijo = 1 // ID del usuario actual

        val nuevaTransaccion = edu.umg.programacion2.appfinanzas.modelo.Transaccion(
            id = 0,
            usuarioId = usuarioIdFijo,
            tipo = tipo,
            monto = monto,
            categoria = categoria,
            descripcion = conceptoFinal,
            fecha = fechaSistema
        )

        lifecycleScope.launch {
            try {
                // Guardamos el movimiento en la base de datos a través de Room
                repositorio.agregar(nuevaTransaccion)

                Toast.makeText(this@FormularioMovimientoActivity, "¡Movimiento guardado con éxito!", Toast.LENGTH_SHORT).show()

                // Cerramos la actividad para regresar al MainActivity y actualizar los totales
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@FormularioMovimientoActivity, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}