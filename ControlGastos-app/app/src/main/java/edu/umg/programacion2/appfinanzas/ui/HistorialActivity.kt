package edu.umg.programacion2.appfinanzas.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.umg.programacion2.appfinanzas.Dependencias
import edu.umg.programacion2.appfinanzas.R
import edu.umg.programacion2.appfinanzas.modelo.Transaccion
import kotlinx.coroutines.launch

class HistorialActivity : AppCompatActivity() {

    private val repositorio = Dependencias.repositorio

    private lateinit var btnRegresar: ImageButton
    private lateinit var spinnerFiltro: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var transaccionAdapter: TransaccionAdapter

    private var listaCompleta: List<Transaccion> = emptyList()
    private val opcionesFiltro = listOf("Todos", "Ingresos", "Gastos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        btnRegresar = findViewById(R.id.btnRegresarHistorial)
        spinnerFiltro = findViewById(R.id.spinnerFiltro)
        recyclerView = findViewById(R.id.recyclerHistorial)

        transaccionAdapter = TransaccionAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transaccionAdapter

        btnRegresar.setOnClickListener { finish() }

        // Configurar Spinner de filtros
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesFiltro)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFiltro.adapter = adapter

        spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filtrarTransacciones(opcionesFiltro[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        lifecycleScope.launch {
            try {
                listaCompleta = repositorio.listar(1)
                // Aplicar el filtro actual al recargar
                val filtroActual = spinnerFiltro.selectedItem?.toString() ?: "Todos"
                filtrarTransacciones(filtroActual)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun filtrarTransacciones(tipoFiltro: String) {
        val listaFiltrada = when (tipoFiltro) {
            "Ingresos" -> listaCompleta.filter { it.tipo.equals("INGRESO", ignoreCase = true) }
            "Gastos" -> listaCompleta.filter { it.tipo.equals("GASTO", ignoreCase = true) }
            else -> listaCompleta // "Todos"
        }
        transaccionAdapter.actualizarListanuevas(listaFiltrada)
    }
}