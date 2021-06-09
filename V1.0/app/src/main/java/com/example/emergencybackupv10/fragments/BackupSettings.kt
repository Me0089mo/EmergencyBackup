package com.example.emergencybackupv10.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emergencybackupv10.Home
import com.example.emergencybackupv10.R
import kotlinx.android.synthetic.main.fragment_backup_settings.*

class BackupSettings : Fragment() {
    var directories = mutableListOf<Pair<Int, Pair<String, String>>>()
    lateinit var backupAdapter: BackupSettingsAdapter

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
        getValuesFromDirList(dirList)

        val recyclerView = view?.findViewById<RecyclerView>(R.id.folder_list)
        backupAdapter = BackupSettingsAdapter(directories)
        val callback = ItemTouchHelperAdapter(backupAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        backupAdapter.setTouchHelper(itemTouchHelper)
        recyclerView.apply {
            this?.layoutManager = LinearLayoutManager(view?.context)
            this?.adapter = backupAdapter
        }
        itemTouchHelper.attachToRecyclerView(recyclerView)
        return view
    }

    override fun onStart() {
        super.onStart()
        btn_add_folder.setOnClickListener { v ->
            (activity as Home).intentForBackupConfiguration()
        }

        btn_save_changes.setOnClickListener { v ->
            (activity as Home).resetDirList()
            val newList: List<String> = backupAdapter.getItems().map { "${it.first}|${it.second.second}|${it.second.first}" }
            (activity as Home).saveDirList(newList.toMutableSet())
        }
    }

    fun updateRecycler(){
        val dirList = (activity as Home).getDirList()
        getValuesFromDirList(dirList)
        this.view?.findViewById<RecyclerView>(R.id.folder_list)?.adapter?.notifyDataSetChanged()
    }

    fun getValuesFromDirList(dirList: MutableSet<String>){
        directories.clear()
        for (dir in dirList) {
            var priority = dir.dropLastWhile { it != '|' }
            priority = priority.substring(0, priority.length-1).dropLastWhile { it != '|' }
            var onlyUri = dir.dropWhile { it != '|' }.substring(1)
            val onlyName = onlyUri.dropWhile { it != '|' }.substring(1)
            onlyUri = onlyUri.substring(0, onlyUri.length-1-onlyName.length)
            priority = priority.substring(0, priority.length-1)
            directories.add(Pair(priority.toInt(), Pair(onlyName, onlyUri)))
        }
        directories.sortBy { it.first }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BackupSettings()
    }

}