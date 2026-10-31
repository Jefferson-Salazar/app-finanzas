package edu.umg.programacion2.appfinanzas.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.umg.programacion2.appfinanzas.Dependencias
import edu.umg.programacion2.appfinanzas.R
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// Actividad principal (Dashboard): Muestra el balance general, ingresos, gastos y accesos a las demás pantallas
class MainActivity : AppCompatActivity() {

    private val repositorio = Dependencias.repositorio

    private lateinit var tvBalanceTotal: TextView
    private lateinit var tvIngresosTotal: TextView
    private lateinit var tvGastosTotal: TextView
    private lateinit var btnRegistrarIngreso: Button
    private lateinit var btnRegistrarGasto: Button
    private lateinit var btnVerHistorial: Button
    private lateinit var btnRegistrarseMain: Button
    private lateinit var btnOcultarSaldo: ImageButton

    // Controla si el saldo está oculto por seguridad (inicia en true)
    private var saldoOculto = true
    private var balanceUltimo: String = "Q 0.00"
    private var ingresosUltimo: String = "Q 0.00"
    private var gastosUltimo: String = "Q 0.00"
    private var balanceColorActual: Int = Color.parseColor("#7F8C8D")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- VALIDACIÓN DE PIN PARA ARRANQUES POSTERIORES ---
        val prefs = getSharedPreferences("AppFinanzasPrefs", Context.MODE_PRIVATE)
        val estaRegistrado = prefs.getBoolean("is_registered", false)

        // Verifica si el Intent autoriza omitir el PIN (cuando venimos de PinActivity)
        val debePedirPin = intent.getBooleanExtra("DEBE_PEDIR_PIN", true)

        // Si ya está registrado y debe pedir PIN, intercepta y abre la pantalla de seguridad
        if (estaRegistrado && debePedirPin) {
            val intentPin = Intent(this, PinActivity::class.java)
            startActivity(intentPin)
            finish() // Cierra MainActivity para que no quede abierta en segundo plano
            return
        }

        setContentView(R.layout.activity_main)

        tvBalanceTotal = findViewById(R.id.tvBalanceTotal)
        tvIngresosTotal = findViewById(R.id.tvIngresosTotal)
        tvGastosTotal = findViewById(R.id.tvGastosTotal)
        btnRegistrarIngreso = findViewById(R.id.btnRegistrarIngreso)
        btnRegistrarGasto = findViewById(R.id.btnRegistrarGasto)
        btnVerHistorial = findViewById(R.id.btnVerHistorial)
        btnRegistrarseMain = findViewById(R.id.btnRegistrarseMain)
        btnOcultarSaldo = findViewById(R.id.btnOcultarSaldo)

        // Verifica si el usuario está registrado antes de permitir registrar un ingreso o gasto
        btnRegistrarIngreso.setOnClickListener {
            verificarYProceder("INGRESO")
        }

        btnRegistrarGasto.setOnClickListener {
            verificarYProceder("GASTO")
        }

        // Abre la pantalla del historial de transacciones
        btnVerHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }

        // Abre la pantalla de registro de usuario
        btnRegistrarseMain.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Evento del botón de ojo para alternar la visibilidad de los saldos y montos
        btnOcultarSaldo.setOnClickListener {
            saldoOculto = !saldoOculto
            val colorGris = Color.parseColor("#7F8C8D")

            if (saldoOculto) {
                tvBalanceTotal.text = "Q •••••"
                tvIngresosTotal.text = "Q •••••"
                tvGastosTotal.text = "Q •••••"

                tvBalanceTotal.setTextColor(colorGris)
                tvIngresosTotal.setTextColor(colorGris)
                tvGastosTotal.setTextColor(colorGris)
            } else {
                tvBalanceTotal.text = balanceUltimo
                tvIngresosTotal.text = ingresosUltimo
                tvGastosTotal.text = gastosUltimo

                tvBalanceTotal.setTextColor(balanceColorActual)
                tvIngresosTotal.setTextColor(Color.parseColor("#27AE60"))
                tvGastosTotal.setTextColor(Color.parseColor("#E74C3C"))
            }
        }
    }

    // Se ejecuta al volver a primer plano para recargar los saldos y verificar el estado del usuario
    override fun onResume() {
        super.onResume()
        cargarResumenFinanciero()

        val prefs = getSharedPreferences("AppFinanzasPrefs", Context.MODE_PRIVATE)
        val estaRegistrado = prefs.getBoolean("is_registered", false)
        val tvSubtitulo = findViewById<TextView>(R.id.tvSubtitulo)

        if (estaRegistrado) {
            btnRegistrarseMain.visibility = View.GONE
            val nombreUsuario = prefs.getString("user_name", "Usuario")
            tvSubtitulo?.text = "Registro financiero de $nombreUsuario"
        } else {
            btnRegistrarseMain.visibility = View.VISIBLE
            tvSubtitulo?.text = "Resumen Financiero"
        }
    }

    // Comprueba si el usuario está registrado; si no, muestra una alerta exigiendo el registro
    private fun verificarYProceder(tipoMovimiento: String) {
        val prefs = getSharedPreferences("AppFinanzasPrefs", Context.MODE_PRIVATE)
        val estaRegistrado = prefs.getBoolean("is_registered", false)

        if (!estaRegistrado) {
            AlertDialog.Builder(this)
                .setTitle("Registro Requerido")
                .setMessage("No puedes registrar movimientos sin estar registrado. ¿Deseas registrarte ahora?")
                .setPositiveButton("Registrarse") { _, _ ->
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        } else {
            val intent = Intent(this, FormularioMovimientoActivity::class.java)
            intent.putExtra("TIPO_MOVIMIENTO", tipoMovimiento)
            startActivity(intent)
        }
    }

    // Consulta las transacciones en la base de datos local, calcula totales y formatea las cantidades en Quetzales
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

                // Define el color del balance según si es positivo (verde), negativo (rojo) o neutro (gris)
                balanceColorActual = when {
                    balance > 0.01 -> Color.parseColor("#27AE60")
                    balance < -0.01 -> Color.parseColor("#E74C3C")
                    else -> Color.parseColor("#7F8C8D")
                }

                val colorGris = Color.parseColor("#7F8C8D")

                if (saldoOculto) {
                    tvBalanceTotal.text = "Q •••••"
                    tvIngresosTotal.text = "Q •••••"
                    tvGastosTotal.text = "Q •••••"

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