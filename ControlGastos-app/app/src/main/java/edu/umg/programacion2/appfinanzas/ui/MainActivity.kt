package edu.umg.programacion2.appfinanzas.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
        val userId = 1

        // Usamos lifecycleScope.launch porque las consultas a Room son suspend
        lifecycleScope.launch {
            val transacciones = repositorio.listar(userId)

            val totalIngresos = transacciones.filter { it.tipo == "INGRESO" }.sumOf { it.monto }
            val totalGastos = transacciones.filter { it.tipo == "GASTO" }.sumOf { it.monto }
            val balanceGeneral = totalIngresos - totalGastos

            tvIngresosTotal.text = String.format("Q %.2f", totalIngresos)
            tvGastosTotal.text = String.format("Q %.2f", totalGastos)
            tvBalanceTotal.text = String.format("Q %.2f", balanceGeneral)

            if (balanceGeneral < 0) {
                tvBalanceTotal.setTextColor(Color.parseColor("#E74C3C")) // Rojo
            } else {
                tvBalanceTotal.setTextColor(Color.parseColor("#27AE60")) // Verde Qallet
            }
        }
    }
}