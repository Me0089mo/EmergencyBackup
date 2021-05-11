package com.example.emergencybackupv10.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import com.example.emergencybackupv10.databinding.ItemFolderViewerBinding
import kotlinx.android.synthetic.main.fragment_backup_settings.view.*
import java.util.*

class BackupSettingsAdapter(
    private val values : List<String>
) : RecyclerView.Adapter<BackupSettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BackupSettingsAdapter.ViewHolder {
        return ViewHolder(ItemFolderViewerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: BackupSettingsAdapter.ViewHolder, position: Int) {
        val item = values[position]
        holder.contentView.text = item
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ItemFolderViewerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val contentView: TextView = binding.contentItems

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

    val simpleItemTouchCallback =
        object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // [3] Do something when an item is moved

                val adapter = recyclerView.adapter
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition

                Collections.swap(values, from, to)
                adapter?.notifyItemMoved(from, to)
                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {

            }
        }
}