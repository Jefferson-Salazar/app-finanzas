package edu.umg.programacion2.appfinanzas.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.umg.programacion2.appfinanzas.R
import edu.umg.programacion2.appfinanzas.modelo.Transaccion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransaccionAdapter(private var listaTransacciones: List<Transaccion>) : RecyclerView.Adapter<TransaccionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategoria: TextView = view.findViewById(R.id.tvItemCategoria)
        val tvConcepto: TextView = view.findViewById(R.id.tvItemConcepto)
        val tvMonto: TextView = view.findViewById(R.id.tvItemMonto)
        val tvFecha: TextView = view.findViewById(R.id.tvItemFecha) // Vinculado al nuevo campo de fecha
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaccion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaccion = listaTransacciones[position]

        holder.tvCategoria.text = transaccion.categoria
        holder.tvConcepto.text = transaccion.descripcion
        holder.tvMonto.text = "Q %.2f".format(transaccion.monto)

        // Aquí formateamos y asignamos la fecha legible
        holder.tvFecha.text = formatearFecha(transaccion.fecha)
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