package com.example.emergencybackupv10.fragments

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.emergencybackupv10.Home
import com.example.emergencybackupv10.R
import kotlinx.android.synthetic.main.fragment_restore.*

class RestoreFragment : Fragment() {
    private var keySelected: Boolean = false
    private var backupSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restore, container, false)
    }

    override fun onStart() {
        super.onStart()
        btn_restore.isEnabled = false
        btn_select_key.setOnClickListener { v ->
            (activity as Home).intentForKeySelection()
        }
        btn_select_backup.setOnClickListener { v ->
            (activity as Home).intentForBackupSelection()
        }
        btn_download.setOnClickListener { v ->
            (activity as Home).downloadBackup()
        }
        btn_restore.setOnClickListener { v ->
            (activity as Home).restoreBackup()
        }
    }

    fun showKeyPath(location: String){
        key_location.setText(location)
        if(!location.contentEquals("Selecciona tu llave"))
            keySelected = true
    }

    fun showBackupPath(location: String){
        backup_location.setText(location)
        if(!location.contentEquals("Selecciona tu respaldo"))
            backupSelected = true
    }

    fun enableRestore(){
        println("Key selected: $keySelected Backup selected: $backupSelected")
        if(backupSelected && keySelected)
            btn_restore.isEnabled = true
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RestoreFragment()

    }


}