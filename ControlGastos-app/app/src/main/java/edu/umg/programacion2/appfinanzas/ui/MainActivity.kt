package edu.umg.programacion2.appfinanzas.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.umg.programacion2.appfinanzas.Dependencias
import edu.umg.programacion2.appfinanzas.R
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val repositorio = Dependencias.repositorio

    private lateinit var tvBalanceTotal: TextView
    private lateinit var tvIngresosTotal: TextView
    private lateinit var tvGastosTotal: TextView
    private lateinit var btnRegistrarIngreso: Button
    private lateinit var btnRegistrarGasto: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var transaccionAdapter: TransaccionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Vincular vistas
        tvBalanceTotal = findViewById(R.id.tvBalanceTotal)
        tvIngresosTotal = findViewById(R.id.tvIngresosTotal)
        tvGastosTotal = findViewById(R.id.tvGastosTotal)
        btnRegistrarIngreso = findViewById(R.id.btnRegistrarIngreso)
        btnRegistrarGasto = findViewById(R.id.btnRegistrarGasto)
        recyclerView = findViewById(R.id.recyclerTransacciones)

        transaccionAdapter = TransaccionAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transaccionAdapter

        // Botón para Registrar Ingreso
        btnRegistrarIngreso.setOnClickListener {
            val intent = Intent(this, FormularioMovimientoActivity::class.java)
            intent.putExtra("TIPO_MOVIMIENTO", "INGRESO")
            startActivity(intent)
        }

        // Botón para Registrar Gasto
        btnRegistrarGasto.setOnClickListener {
            val intent = Intent(this, FormularioMovimientoActivity::class.java)
            intent.putExtra("TIPO_MOVIMIENTO", "GASTO")
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

                val balance = totalIngresos - totalGastos

                // Mostrar totales
                tvIngresosTotal.text = "Q %.2f".format(totalIngresos)
                tvGastosTotal.text = "Q %.2f".format(totalGastos)
                tvBalanceTotal.text = "Q %.2f".format(balance)

                // Actualizar colores del balance
                when {
                    balance > 0.01 -> tvBalanceTotal.setTextColor(Color.parseColor("#27AE60"))
                    balance < -0.01 -> tvBalanceTotal.setTextColor(Color.parseColor("#E74C3C"))
                    else -> tvBalanceTotal.setTextColor(Color.parseColor("#7F8C8D"))
                }

                // Actualizar la lista
                transaccionAdapter.actualizarListanuevas(transacciones)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}