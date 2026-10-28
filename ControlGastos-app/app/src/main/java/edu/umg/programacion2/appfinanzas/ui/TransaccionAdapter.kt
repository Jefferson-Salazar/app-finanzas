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

class TransaccionAdapter(
    private var listaTransacciones: List<Transaccion>,
    private val onClick: (Transaccion) -> Unit,
    private val onLongClick: () -> Unit,
    private val onSelectionChanged: (Int) -> Unit
) : RecyclerView.Adapter<TransaccionAdapter.ViewHolder>() {

    val idsSeleccionados = mutableSetOf<Int>()
    var enModoSeleccion = false

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.checkboxSeleccion)
        val tvCategoria: TextView = view.findViewById(R.id.tvItemCategoria)
        val tvConcepto: TextView = view.findViewById(R.id.tvItemConcepto)
        val tvMonto: TextView = view.findViewById(R.id.tvItemMonto)
        val tvFecha: TextView = view.findViewById(R.id.tvItemFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaccion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaccion = listaTransacciones[position]

        holder.tvCategoria.text = transaccion.categoria
        holder.tvConcepto.text = transaccion.descripcion

        val formatoMoneda = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        val montoFormateado = formatoMoneda.format(transaccion.monto)

        if (transaccion.tipo.equals("INGRESO", ignoreCase = true)) {
            holder.tvMonto.text = "+ Q $montoFormateado"
            holder.tvMonto.setTextColor(Color.parseColor("#27AE60"))
        } else {
            holder.tvMonto.text = "- Q $montoFormateado"
            holder.tvMonto.setTextColor(Color.parseColor("#E74C3C"))
        }

        holder.tvFecha.text = formatearFecha(transaccion.fecha)

        // Mostrar u ocultar checkbox según el modo selección
        if (enModoSeleccion) {
            holder.checkbox.visibility = View.VISIBLE
            holder.checkbox.isChecked = idsSeleccionados.contains(transaccion.id)
        } else {
            holder.checkbox.visibility = View.GONE
            holder.checkbox.isChecked = false
        }

        // Clic normal
        holder.itemView.setOnClickListener {
            if (enModoSeleccion) {
                toggleSeleccion(transaccion.id)
            } else {
                onClick(transaccion)
            }
        }

        // Clic largo para activar modo selección
        holder.itemView.setOnLongClickListener {
            if (!enModoSeleccion) {
                enModoSeleccion = true
                onLongClick()
            }
            toggleSeleccion(transaccion.id)
            true
        }
    }

    private fun toggleSeleccion(id: Int) {
        if (idsSeleccionados.contains(id)) {
            idsSeleccionados.remove(id)
        } else {
            idsSeleccionados.add(id)
        }
        notifyDataSetChanged()
        onSelectionChanged(idsSeleccionados.size)
    }

    fun seleccionarTodos() {
        idsSeleccionados.clear()
        for (t in listaTransacciones) {
            idsSeleccionados.add(t.id)
        }
        notifyDataSetChanged()
        onSelectionChanged(idsSeleccionados.size)
    }

    fun limpiarSeleccion() {
        enModoSeleccion = false
        idsSeleccionados.clear()
        notifyDataSetChanged()
        onSelectionChanged(0)
    }

    override fun getItemCount(): Int = listaTransacciones.size

    private fun formatearFecha(milisegundosStr: String): String {
        return try {
            val milisegundos = milisegundosStr.toLong()
            val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            formato.format(Date(milisegundos))
        } catch (e: Exception) {
            milisegundosStr
        }
    }

    fun actualizarListanuevas(nuevaLista: List<Transaccion>) {
        listaTransacciones = nuevaLista
        notifyDataSetChanged()
    }
}