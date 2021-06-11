package com.example.emergencybackupv10.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.emergencybackupv10.Home
import com.example.emergencybackupv10.R
import com.example.emergencybackupv10.utils.AlertUtils
import kotlinx.android.synthetic.main.fragment_restore.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RestoreFragment : Fragment() {
    private val alertUtils = AlertUtils()

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
            when {
                key_location.text.toString().contentEquals("") ->
                    alertUtils.topToast(this.requireContext(), "No ha seleccionado su llave")
                backup_location.text.toString().contentEquals("") ->
                    alertUtils.topToast(this.requireContext(), "No ha seleccionado su respaldo")
                else -> (activity as Home).restoreBackup()
            }
        }
    }

    fun showKeyPath(location: String){
        key_location.setText(location)
    }

    fun showBackupPath(location: String){
        backup_location.setText(location)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RestoreFragment()
    }


}