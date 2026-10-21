package edu.umg.programacion2.appfinanzas.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.umg.programacion2.appfinanzas.Dependencias
import edu.umg.programacion2.appfinanzas.R
import edu.umg.programacion2.appfinanzas.modelo.Transaccion
import kotlinx.coroutines.launch

class FormularioMovimientoActivity : AppCompatActivity() {

    private val repositorio = Dependencias.repositorio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_movimiento)

        val etConcepto = findViewById<EditText>(R.id.etConcepto)
        val etMonto = findViewById<EditText>(R.id.etMonto)
        val rbIngreso = findViewById<RadioButton>(R.id.rbIngreso)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        btnGuardar.setOnClickListener {
            val concepto = etConcepto.text.toString().trim()
            val montoTexto = etMonto.text.toString().trim()

            if (concepto.isEmpty() || montoTexto.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val monto = montoTexto.toDoubleOrNull() ?: 0.0
            val tipo = if (rbIngreso.isChecked) "INGRESO" else "GASTO"

            val nuevaTransaccion = Transaccion(
                id = 0,
                usuarioId = 1,
                descripcion = concepto,
                monto = monto,
                tipo = tipo,
                categoria = "General",
                fecha = "2026-10-21"
            )

            lifecycleScope.launch {
                repositorio.agregar(nuevaTransaccion)
                Toast.makeText(this@FormularioMovimientoActivity, "¡Movimiento guardado!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}