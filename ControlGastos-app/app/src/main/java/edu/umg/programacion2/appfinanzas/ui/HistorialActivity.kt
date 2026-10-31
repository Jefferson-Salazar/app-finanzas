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

// Actividad encargada de mostrar el listado completo del historial de transacciones con opciones de filtro y eliminación múltiple
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
        btnEliminarSeleccionados = findViewById(R.id.btnEliminarSeleccionados) // Botón de basura para borrar seleccionados
        btnSeleccionarTodo = findViewById(R.id.btnSeleccionarTodo) // Botón para marcar todos los elementos
        spinnerFiltro = findViewById(R.id.spinnerFiltro)
        recyclerView = findViewById(R.id.recyclerHistorial)
        tvHistorialVacio = findViewById(R.id.tvHistorialVacio) // Texto que se muestra si no hay registros

        // Configura el adaptador del RecyclerView y define las acciones al hacer clic, clic largo o seleccionar ítems
        transaccionAdapter = TransaccionAdapter(
            listaTransacciones = emptyList(),
            onClick = { transaccionSeleccionada ->
                // Clic normal: Abre el formulario pasando todos los datos de la transacción para editarla
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
                // Clic largo: Activa el modo selección y muestra los botones superiores de acción
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

        // Selecciona todos los elementos de la lista al hacer clic
        btnSeleccionarTodo.setOnClickListener {
            transaccionAdapter.seleccionarTodos()
        }

        // Muestra un cuadro de diálogo de confirmación antes de eliminar las transacciones seleccionadas
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

        // Configura el menú desplegable (Spinner) para filtrar por tipo de movimiento
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

    // Se ejecuta cada vez que la pantalla vuelve a primer plano para recargar los datos actualizados
    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    // Consulta la base de datos en segundo plano para obtener todas las transacciones del usuario
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

    // Filtra la lista completa según la opción seleccionada en el Spinner (Todos, Ingresos o Gastos)
    private fun filtrarTransacciones(tipoFiltro: String) {
        val listaFiltrada = when (tipoFiltro) {
            "Ingresos" -> listaCompleta.filter { it.tipo.equals("INGRESO", ignoreCase = true) }
            "Gastos" -> listaCompleta.filter { it.tipo.equals("GASTO", ignoreCase = true) }
            else -> listaCompleta
        }

        transaccionAdapter.actualizarListanuevas(listaFiltrada)

        // Muestra un mensaje si la lista está vacía u oculta el texto si hay registros
        if (listaFiltrada.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvHistorialVacio.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            tvHistorialVacio.visibility = View.GONE
        }
    }

    // Elimina de la base de datos local los elementos que el usuario seleccionó
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
                cargarDatos() // Recarga el listado tras la eliminación
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@HistorialActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}