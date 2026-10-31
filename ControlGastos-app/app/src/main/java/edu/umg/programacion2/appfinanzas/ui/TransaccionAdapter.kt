package edu.umg.programacion2.appfinanzas.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.umg.programacion2.appfinanzas.R
import edu.umg.programacion2.appfinanzas.modelo.Transaccion
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Adaptador del RecyclerView: Se encarga de transformar cada objeto Transaccion en una tarjeta visual dentro del historial
class TransaccionAdapter(
    private var listaTransacciones: List<Transaccion>,
    private val onClick: (Transaccion) -> Unit, // Acción al hacer clic normal (editar)
    private val onLongClick: () -> Unit,          // Acción al hacer clic largo (activar selección múltiple)
    private val onSelectionChanged: (Int) -> Unit // Notifica cuántos elementos están seleccionados
) : RecyclerView.Adapter<TransaccionAdapter.ViewHolder>() {

    val idsSeleccionados = mutableSetOf<Int>()
    var enModoSeleccion = false

    // Clase ViewHolder: Guarda las referencias de los elementos visuales de cada tarjeta individual (item_transaccion.xml)
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.checkboxSeleccion)
        val tvCategoria: TextView = view.findViewById(R.id.tvItemCategoria)
        val tvConcepto: TextView = view.findViewById(R.id.tvItemConcepto)
        val tvMonto: TextView = view.findViewById(R.id.tvItemMonto)
        val tvFecha: TextView = view.findViewById(R.id.tvItemFecha)
    }

    // Infla el diseño XML de la tarjeta individual para que pueda mostrarse en la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaccion, parent, false)
        return ViewHolder(view)
    }

    // Rinde / asigna los datos de cada transacción a los componentes visuales de la tarjeta según su posición
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaccion = listaTransacciones[position]

        holder.tvCategoria.text = transaccion.categoria
        holder.tvConcepto.text = transaccion.descripcion

        // Formatea el monto con separadores de miles y dos decimales
        val formatoMoneda = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        val montoFormateado = formatoMoneda.format(transaccion.monto)

        // Asigna colores e iconos diferentes según si es Ingreso (verde) o Gasto (rojo)
        if (transaccion.tipo.equals("INGRESO", ignoreCase = true)) {
            holder.tvMonto.text = "+ Q $montoFormateado"
            holder.tvMonto.setTextColor(Color.parseColor("#27AE60"))
        } else {
            holder.tvMonto.text = "- Q $montoFormateado"
            holder.tvMonto.setTextColor(Color.parseColor("#E74C3C"))
        }

        holder.tvFecha.text = formatearFecha(transaccion.fecha)

        // Muestra u oculta la casilla de verificación según el modo de selección múltiple
        if (enModoSeleccion) {
            holder.checkbox.visibility = View.VISIBLE
            holder.checkbox.isChecked = idsSeleccionados.contains(transaccion.id)
        } else {
            holder.checkbox.visibility = View.GONE
            holder.checkbox.isChecked = false
        }

        // Clic normal: Abre la edición o marca el elemento si ya está en modo selección
        holder.itemView.setOnClickListener {
            if (enModoSeleccion) {
                toggleSeleccion(transaccion.id)
            } else {
                onClick(transaccion)
            }
        }

        // Clic largo: Activa el modo selección múltiple y marca el primer elemento presionado
        holder.itemView.setOnLongClickListener {
            if (!enModoSeleccion) {
                enModoSeleccion = true
                onLongClick()
            }
            toggleSeleccion(transaccion.id)
            true
        }
    }

    // Agrega o quita un ID de la lista de seleccionados y refresca la vista
    private fun toggleSeleccion(id: Int) {
        if (idsSeleccionados.contains(id)) {
            idsSeleccionados.remove(id)
        } else {
            idsSeleccionados.add(id)
        }
        notifyDataSetChanged()
        onSelectionChanged(idsSeleccionados.size)
    }

    // Marca absolutamente todas las transacciones de la lista actual
    fun seleccionarTodos() {
        idsSeleccionados.clear()
        for (t in listaTransacciones) {
            idsSeleccionados.add(t.id)
        }
        notifyDataSetChanged()
        onSelectionChanged(idsSeleccionados.size)
    }

    // Limpia la selección y apaga el modo selección múltiple
    fun limpiarSeleccion() {
        enModoSeleccion = false
        idsSeleccionados.clear()
        notifyDataSetChanged()
        onSelectionChanged(0)
    }

    // Devuelve el número total de elementos que tiene el RecyclerView
    override fun getItemCount(): Int = listaTransacciones.size

    // Convierte los milisegundos guardados en la base de datos a un formato de fecha y hora legible (DD/MM/YYYY HH:MM)
    private fun formatearFecha(milisegundosStr: String): String {
        return try {
            // Convierte el texto guardado en la base de datos a un número largo (milisegundos)
            val milisegundos = milisegundosStr.toLong()

            // Define el formato visual que deseas mostrar: Día / Mes / Año y Hora : Minutos
            val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            // Traduce los milisegundos a una fecha real y la devuelve en texto
            formato.format(Date(milisegundos))
        } catch (e: Exception) {
            // Por seguridad, si llega a haber algún error, devuelve el texto original
            milisegundosStr
        }
    }

    // Actualiza la lista de elementos y le avisa al adaptador que debe redibujar la interfaz
    fun actualizarListanuevas(nuevaLista: List<Transaccion>) {
        listaTransacciones = nuevaLista
        notifyDataSetChanged()
    }
}