package edu.umg.programacion2.appfinanzas.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.umg.programacion2.appfinanzas.Dependencias
import edu.umg.programacion2.appfinanzas.R
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val repositorio = Dependencias.repositorio

    private lateinit var tvBalanceTotal: TextView
    private lateinit var tvIngresosTotal: TextView
    private lateinit var tvGastosTotal: TextView
    private lateinit var btnRegistrarIngreso: Button
    private lateinit var btnRegistrarGasto: Button
    private lateinit var btnVerHistorial: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvBalanceTotal = findViewById(R.id.tvBalanceTotal)
        tvIngresosTotal = findViewById(R.id.tvIngresosTotal)
        tvGastosTotal = findViewById(R.id.tvGastosTotal)
        btnRegistrarIngreso = findViewById(R.id.btnRegistrarIngreso)
        btnRegistrarGasto = findViewById(R.id.btnRegistrarGasto)
        btnVerHistorial = findViewById(R.id.btnVerHistorial)

        btnRegistrarIngreso.setOnClickListener {
            val intent = Intent(this, FormularioMovimientoActivity::class.java)
            intent.putExtra("TIPO_MOVIMIENTO", "INGRESO")
            startActivity(intent)
        }

        btnRegistrarGasto.setOnClickListener {
            val intent = Intent(this, FormularioMovimientoActivity::class.java)
            intent.putExtra("TIPO_MOVIMIENTO", "GASTO")
            startActivity(intent)
        }

        btnVerHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarResumenFinanciero()
    }

    private fun cargarResumenFinanciero() {
        lifecycleScope.launch {
            try {
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

                val formatoMoneda = NumberFormat.getNumberInstance(Locale.US).apply {
                    minimumFractionDigits = 2
                    maximumFractionDigits = 2
                }

                val balance = totalIngresos - totalGastos

                tvIngresosTotal.text = "Q ${formatoMoneda.format(totalIngresos)}"
                tvGastosTotal.text = "Q ${formatoMoneda.format(totalGastos)}"
                tvBalanceTotal.text = "Q ${formatoMoneda.format(balance)}"

                when {
                    balance > 0.01 -> tvBalanceTotal.setTextColor(Color.parseColor("#27AE60"))
                    balance < -0.01 -> tvBalanceTotal.setTextColor(Color.parseColor("#E74C3C"))
                    else -> tvBalanceTotal.setTextColor(Color.parseColor("#7F8C8D"))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}