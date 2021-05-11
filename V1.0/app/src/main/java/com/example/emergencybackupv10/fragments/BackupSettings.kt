package com.example.emergencybackupv10.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emergencybackupv10.Home
import com.example.emergencybackupv10.R
import kotlinx.android.synthetic.main.fragment_backup_settings.*
import java.util.*
import java.util.regex.Pattern

class BackupSettings : Fragment() {
    var namesList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_backup_settings, container, false)
        val dirList = (activity as Home).getDirList()
        //var namesList = mutableListOf<String>()
        for (dir in dirList){
            val ind = dir.indexOfLast { it == '%' }
            namesList.add(dir.drop(ind+3))
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.folder_list)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = BackupSettingsAdapter(namesList)
        }
        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(recyclerView)
        return view
    }

    override fun onStart() {
        super.onStart()
        btn_add_folder.setOnClickListener { v ->
            (activity as Home).addFolderToBackup()
            val dirList = (activity as Home).getDirList()
            namesList.clear()
            for (dir in dirList){
                val ind = dir.indexOfLast { it == '%' }
                namesList.add(dir.drop(ind+3))
            }
            this.view?.findViewById<RecyclerView>(R.id.folder_list)?.adapter?.notifyDataSetChanged()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BackupSettings()
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

                Collections.swap(namesList, from, to)
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