package com.example.emergencybackupv10.fragments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.emergencybackupv10.databinding.ItemFolderViewerBinding
import com.example.emergencybackupv10.databinding.FragmentBackupSettingsBinding

class FilesRecyclerViewAdapter(
    private val values: List<String>
) : RecyclerView.Adapter<FilesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemFolderViewerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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

}