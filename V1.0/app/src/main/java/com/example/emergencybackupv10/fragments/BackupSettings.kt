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
        for (dir in dirList){
            val ind = dir.indexOfLast { it == '%' }
            namesList.add(dir.drop(ind+3))
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.folder_list)
        val backupAdapter = BackupSettingsAdapter(namesList)
        val callback = ItemTouchHelperAdapter(backupAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        backupAdapter.setTouchHelper(itemTouchHelper)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = backupAdapter
        }
        itemTouchHelper.attachToRecyclerView(recyclerView)
        return view
    }

    override fun onStart() {
        super.onStart()

        btn_add_folder.setOnClickListener { v ->
            (activity as Home).intentForBackupConfiguration()
            val dirList = (activity as Home).getDirList()
            namesList.clear()
            for (dir in dirList){
                val ind = dir.indexOfLast { it == '%' }
                namesList.add(dir.drop(ind+3))
                println(dir)
            }
            this.view?.findViewById<RecyclerView>(R.id.folder_list)?.adapter?.notifyDataSetChanged()
        }

        btn_save_changes.setOnClickListener { v ->
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BackupSettings()
    }

}