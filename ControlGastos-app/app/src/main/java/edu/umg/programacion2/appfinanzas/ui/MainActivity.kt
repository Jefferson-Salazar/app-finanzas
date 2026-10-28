package edu.umg.programacion2.appfinanzas.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
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
    private lateinit var btnOcultarSaldo: ImageButton

    // Inicia en true para que por defecto aparezca oculto
    private var saldoOculto = true
    private var balanceUltimo: String = "Q 0.00"
    private var ingresosUltimo: String = "Q 0.00"
    private var gastosUltimo: String = "Q 0.00"
    private var balanceColorActual: Int = Color.parseColor("#7F8C8D")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvBalanceTotal = findViewById(R.id.tvBalanceTotal)
        tvIngresosTotal = findViewById(R.id.tvIngresosTotal)
        tvGastosTotal = findViewById(R.id.tvGastosTotal)
        btnRegistrarIngreso = findViewById(R.id.btnRegistrarIngreso)
        btnRegistrarGasto = findViewById(R.id.btnRegistrarGasto)
        btnVerHistorial = findViewById(R.id.btnVerHistorial)
        btnOcultarSaldo = findViewById(R.id.btnOcultarSaldo)

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

        // Evento del botón Ojo para alternar visibilidad de montos
        btnOcultarSaldo.setOnClickListener {
            saldoOculto = !saldoOculto
            val colorGris = Color.parseColor("#7F8C8D")

            if (saldoOculto) {
                tvBalanceTotal.text = "Q *****"
                tvIngresosTotal.text = "Q *****"
                tvGastosTotal.text = "Q *****"

                // Poner todos en gris cuando sean asteriscos
                tvBalanceTotal.setTextColor(colorGris)
                tvIngresosTotal.setTextColor(colorGris)
                tvGastosTotal.setTextColor(colorGris)
            } else {
                tvBalanceTotal.text = balanceUltimo
                tvIngresosTotal.text = ingresosUltimo
                tvGastosTotal.text = gastosUltimo

                // Restaurar colores originales
                tvBalanceTotal.setTextColor(balanceColorActual)
                tvIngresosTotal.setTextColor(Color.parseColor("#27AE60"))
                tvGastosTotal.setTextColor(Color.parseColor("#E74C3C"))
            }
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

                ingresosUltimo = "Q ${formatoMoneda.format(totalIngresos)}"
                gastosUltimo = "Q ${formatoMoneda.format(totalGastos)}"
                balanceUltimo = "Q ${formatoMoneda.format(balance)}"

                balanceColorActual = when {
                    balance > 0.01 -> Color.parseColor("#27AE60")
                    balance < -0.01 -> Color.parseColor("#E74C3C")
                    else -> Color.parseColor("#7F8C8D")
                }

                val colorGris = Color.parseColor("#7F8C8D")

                // Por defecto oculto con color gris en todas las tarjetas
                if (saldoOculto) {
                    tvBalanceTotal.text = "Q *****"
                    tvIngresosTotal.text = "Q *****"
                    tvGastosTotal.text = "Q *****"

                    tvBalanceTotal.setTextColor(colorGris)
                    tvIngresosTotal.setTextColor(colorGris)
                    tvGastosTotal.setTextColor(colorGris)
                } else {
                    tvIngresosTotal.text = ingresosUltimo
                    tvGastosTotal.text = gastosUltimo
                    tvBalanceTotal.text = balanceUltimo

                    tvBalanceTotal.setTextColor(balanceColorActual)
                    tvIngresosTotal.setTextColor(Color.parseColor("#27AE60"))
                    tvGastosTotal.setTextColor(Color.parseColor("#E74C3C"))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}