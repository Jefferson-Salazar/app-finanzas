package edu.umg.programacion2.appfinanzas.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.umg.programacion2.appfinanzas.R
import edu.umg.programacion2.appfinanzas.modelo.Transaccion

class TransaccionAdapter(private var transacciones: List<Transaccion>) :
    RecyclerView.Adapter<TransaccionAdapter.TransaccionViewHolder>() {

    class TransaccionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategoria: TextView = view.findViewById(R.id.tvItemCategoria)
        val tvConcepto: TextView = view.findViewById(R.id.tvItemConcepto)
        val tvMonto: TextView = view.findViewById(R.id.tvItemMonto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaccion, parent, false)
        return TransaccionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
        val transaccion = transacciones[position]
        holder.tvCategoria.text = transaccion.categoria
        holder.tvConcepto.text = transaccion.descripcion

        if (transaccion.tipo.equals("INGRESO", ignoreCase = true)) {
            holder.tvMonto.text = "+ Q %.2f".format(transaccion.monto)
            holder.tvMonto.setTextColor(Color.parseColor("#27AE60")) // Verde
        } else {
            holder.tvMonto.text = "- Q %.2f".format(transaccion.monto)
            holder.tvMonto.setTextColor(Color.parseColor("#E74C3C")) // Rojo
        }
    }

    override fun getItemCount(): Int = transacciones.size

    fun actualizarListanuevas(nuevaLista: List<Transaccion>) {
        transacciones = nuevaLista
        notifyDataSetChanged()
    }
}