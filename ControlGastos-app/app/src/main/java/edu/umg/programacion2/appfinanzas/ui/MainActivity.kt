package edu.umg.programacion2.appfinanzas.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.umg.programacion2.appfinanzas.Dependencias
import edu.umg.programacion2.appfinanzas.R
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val repositorio = Dependencias.repositorio

    private lateinit var tvBalanceTotal: TextView
    private lateinit var tvIngresosTotal: TextView
    private lateinit var tvGastosTotal: TextView
    private lateinit var btnNuevoMovimiento: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Vincular vistas con sus IDs del XML
        tvBalanceTotal = findViewById(R.id.tvBalanceTotal)
        tvIngresosTotal = findViewById(R.id.tvIngresosTotal)
        tvGastosTotal = findViewById(R.id.tvGastosTotal)
        btnNuevoMovimiento = findViewById(R.id.btnNuevoMovimiento)

        // Botón para abrir el formulario de nuevo movimiento
        btnNuevoMovimiento.setOnClickListener {
            val intent = Intent(this, FormularioMovimientoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar el resumen financiero al regresar a la pantalla principal
        cargarResumenFinanciero()
    }

    private fun cargarResumenFinanciero() {
        lifecycleScope.launch {
            try {
                // Obtener transacciones del usuario 1 desde Room
                val transacciones = repositorio.listar(1)

                var totalIngresos = 0.0
                var totalGastos = 0.0

                for (t in transacciones) {
                    if (t.tipo.equals("INGRESO", ignoreCase = true)) {
                        totalIngresos += t.monto
                    } else {
                        totalGastos += t.monto
                    }
                }

                val balance = totalIngresos - totalGastos

                // Formato en Quetzales (Q)
                tvIngresosTotal.text = "Q %.2f".format(totalIngresos)
                tvGastosTotal.text = "Q %.2f".format(totalGastos)
                tvBalanceTotal.text = "Q %.2f".format(balance)

                // Cambiar el color del Balance General según su valor
                when {
                    balance > 0 -> {
                        // Verde para positivo
                        tvBalanceTotal.setTextColor(android.graphics.Color.parseColor("#27AE60"))
                    }
                    balance < 0 -> {
                        // Rojo para negativo
                        tvBalanceTotal.setTextColor(android.graphics.Color.parseColor("#E74C3C"))
                    }
                    else -> {
                        // Gris neutro para cero
                        tvBalanceTotal.setTextColor(android.graphics.Color.parseColor("#7F8C8D"))
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}