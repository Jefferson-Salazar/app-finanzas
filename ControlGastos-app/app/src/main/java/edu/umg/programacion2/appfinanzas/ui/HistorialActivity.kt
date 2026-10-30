package edu.umg.programacion2.appfinanzas.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var btnEliminarSeleccionados: ImageButton
    private lateinit var btnSeleccionarTodo: ImageButton
    private lateinit var spinnerFiltro: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvHistorialVacio: TextView
    private lateinit var transaccionAdapter: TransaccionAdapter

    private var listaCompleta: List<Transaccion> = emptyList()
    private val opcionesFiltro = listOf("Todos", "Ingresos", "Gastos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        btnRegresar = findViewById(R.id.btnRegresarHistorial)
        btnEliminarSeleccionados = findViewById(R.id.btnEliminarSeleccionados) // Bote de basura arriba a la derecha
        btnSeleccionarTodo = findViewById(R.id.btnSeleccionarTodo) // Botón para seleccionar todo arriba a la derecha
        spinnerFiltro = findViewById(R.id.spinnerFiltro)
        recyclerView = findViewById(R.id.recyclerHistorial)
        tvHistorialVacio = findViewById(R.id.tvHistorialVacio) // Mensaje de vacío

        transaccionAdapter = TransaccionAdapter(
            listaTransacciones = emptyList(),
            onClick = { transaccionSeleccionada ->
                // Clic normal: Abrir edición pasando TODOS los datos correctamente
                val intent = Intent(this, FormularioMovimientoActivity::class.java).apply {
                    putExtra("EDITAR_MODO", true)
                    putExtra("TRANSACCION_ID", transaccionSeleccionada.id)
                    putExtra("TIPO_MOVIMIENTO", transaccionSeleccionada.tipo)
                    putExtra("CATEGORIA", transaccionSeleccionada.categoria)
                    putExtra("MONTO", transaccionSeleccionada.monto)
                    putExtra("DESCRIPCION", transaccionSeleccionada.descripcion)
                    putExtra("FECHA", transaccionSeleccionada.fecha)
                }
                startActivity(intent)
            },
            onLongClick = {
                // Al hacer clic largo, mostramos los botones superiores
                btnEliminarSeleccionados.visibility = View.VISIBLE
                btnSeleccionarTodo.visibility = View.VISIBLE
            },
            onSelectionChanged = { cantidad ->
                if (cantidad == 0 && !transaccionAdapter.enModoSeleccion) {
                    btnEliminarSeleccionados.visibility = View.GONE
                    btnSeleccionarTodo.visibility = View.GONE
                }
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transaccionAdapter

        btnRegresar.setOnClickListener { finish() }

        // Botón Seleccionar Todo
        btnSeleccionarTodo.setOnClickListener {
            transaccionAdapter.seleccionarTodos()
        }

        // Botón Eliminar con Diálogo de Confirmación
        btnEliminarSeleccionados.setOnClickListener {
            val cantidad = transaccionAdapter.idsSeleccionados.size
            if (cantidad > 0) {
                val mensaje = if (cantidad == 1) {
                    "¿Seguro que quieres eliminar el elemento seleccionado? Esta acción es irreversible."
                } else {
                    "¿Seguro que quieres eliminar los $cantidad elementos seleccionados? Esta acción es irreversible."
                }

                AlertDialog.Builder(this)
                    .setTitle("Eliminar transacciones")
                    .setMessage(mensaje)
                    .setPositiveButton("Sí, eliminar") { _, _ ->
                        eliminarElementosSeleccionados()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }

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
            else -> listaCompleta
        }

        transaccionAdapter.actualizarListanuevas(listaFiltrada)

        // Control de visibilidad para el estado vacío
        if (listaFiltrada.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvHistorialVacio.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            tvHistorialVacio.visibility = View.GONE
        }
    }

    private fun eliminarElementosSeleccionados() {
        val idsAEliminar = transaccionAdapter.idsSeleccionados.toList()
        lifecycleScope.launch {
            try {
                for (id in idsAEliminar) {
                    repositorio.eliminar(id)
                }
                Toast.makeText(this@HistorialActivity, "Transacciones eliminadas", Toast.LENGTH_SHORT).show()
                transaccionAdapter.limpiarSeleccion()
                btnEliminarSeleccionados.visibility = View.GONE
                btnSeleccionarTodo.visibility = View.GONE
                cargarDatos()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@HistorialActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}