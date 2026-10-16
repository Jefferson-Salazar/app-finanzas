package edu.umg.programacion2.appfinanzas.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.umg.programacion2.appfinanzas.R
import edu.umg.programacion2.appfinanzas.dao.Database

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: Database
    private lateinit var tvBalanceTotal: TextView
    private lateinit var tvIngresosTotal: TextView
    private lateinit var tvGastosTotal: TextView
    private lateinit var btnNuevoMovimiento: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = Database(this)

        tvBalanceTotal = findViewById(R.id.tvBalanceTotal)
        tvIngresosTotal = findViewById(R.id.tvIngresosTotal)
        tvGastosTotal = findViewById(R.id.tvGastosTotal)
        btnNuevoMovimiento = findViewById(R.id.btnNuevoMovimiento)

        btnNuevoMovimiento.setOnClickListener {
            Toast.makeText(this, "Próximamente: Formulario de Registro", Toast.LENGTH_SHORT).show()
        }

        actualizarResumenFinanciero()
    }

    override fun onResume() {
        super.onResume()
        actualizarResumenFinanciero()
    }

    private fun actualizarResumenFinanciero() {
        // Le pasamos el usuarioId (ej: 1) y el tipo de movimiento
        val userId = 1
        val totalIngresos = dbHelper.obtenerTotalPorTipo(userId, "INGRESO")
        val totalGastos = dbHelper.obtenerTotalPorTipo(userId, "GASTO")
        val balanceGeneral = totalIngresos - totalGastos

        tvIngresosTotal.text = String.format("Q %.2f", totalIngresos)
        tvGastosTotal.text = String.format("Q %.2f", totalGastos)
        tvBalanceTotal.text = String.format("Q %.2f", balanceGeneral)

        // Si el saldo es negativo, lo muestra en rojo
        if (balanceGeneral < 0) {
            tvBalanceTotal.setTextColor(Color.parseColor("#E74C3C")) // Rojo
        } else {
            tvBalanceTotal.setTextColor(Color.parseColor("#27AE60")) // Verde Qallet
        }
    }
}