package com.example.emergencybackupv10.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import com.example.emergencybackupv10.Backup
import com.example.emergencybackupv10.CifradorAESCBC
import com.example.emergencybackupv10.R
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.File

private const val ARG_BU_AVAILABLE = "backUpAvailable"

class HomeFragment : Fragment() {

    private var backUpOnCloud: Boolean = false
    private var cipheredDataPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            backUpOnCloud = it.getBoolean(ARG_BU_AVAILABLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onStart() {
        super.onStart()
        setText()
        btnSelect.setOnClickListener { v: View? ->
            //Creating directory for ciphered data
            val cipherDataDirectory = File(this.context?.filesDir, "CipheredData")
            if(cipherDataDirectory.mkdir())
                cipheredDataPath = cipherDataDirectory.absolutePath

            //Creating document picker
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivityForResult(intent, 42)
        }

        btnCifrar.setOnClickListener { v: View? ->
            val createBackup = Backup(this.requireContext().applicationContext, cipheredDataPath!!)
            createBackup.readConfiguration()
            createBackup.readAll()
        }

    }

    private fun setText() {
        if(backUpOnCloud){
            text_home.text = "Hay un respaldo en la nube"
        }else{
            text_home.text = "No hay un respaldo pendiente\n Hurray?"
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(backUpOnCloud: Boolean) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_BU_AVAILABLE, backUpOnCloud)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 42 && resultCode == Activity.RESULT_OK) {
            data?.data.also { uri ->
                uri?.let {
                    val newBackup = Backup(this.requireContext().applicationContext, cipheredDataPath!!)
                    newBackup.createConfiguration(it)
                }
            }
        }
    }
}