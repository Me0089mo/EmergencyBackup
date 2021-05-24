package com.example.emergencybackupv10.fragments

import android.os.Bundle
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
    var directories = mutableListOf<Pair<String, String>>()
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
            val newList: List<String> = backupAdapter.getItems().map { it.second }
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
            val ind = dir.indexOfLast { it == '%' }
            directories.add(Pair(dir.drop(ind + 3), dir))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BackupSettings()
    }

}