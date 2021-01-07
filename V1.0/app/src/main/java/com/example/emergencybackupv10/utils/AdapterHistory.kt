package com.example.emergencybackupv10.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.emergencybackupv10.R

class AdapterHistory() : RecyclerView.Adapter<AdapterHistory.ViewHolderHistory>() {
    val ListaElementos = ArrayList<UserActivity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHistory {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.cards_list, null, false)
        return ViewHolderHistory(view)
    }

    override fun getItemCount(): Int {
        return ListaElementos.size
    }

    override fun onBindViewHolder(holder: ViewHolderHistory, position: Int) {
        holder.asignarDatos(ListaElementos.get(position))
    }

    class ViewHolderHistory(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descripcion:TextView = itemView.findViewById(R.id.txtDescripcion)
        val tiempo:TextView = itemView.findViewById(R.id.txtTiempo)
        val detalles:TextView = itemView.findViewById(R.id.txtDetalles)

        fun asignarDatos(elemento: UserActivity){
            descripcion.text = elemento.descripcion
            tiempo.text = elemento.tiempo
            detalles.text = elemento.detalles
        }
    }
}