package com.example.emergencybackupv10.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import com.example.emergencybackupv10.Backup
import com.example.emergencybackupv10.DescifradorAES_CFB
import com.example.emergencybackupv10.R
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.File

private const val ARG_BU_AVAILABLE = "backUpAvailable"

class HomeFragment : Fragment() {

    private var backUpOnCloud: Boolean = false
    private var cipheredDataPath: String? = null
    private var decipheredDataPath : String? = null
    private var publicKeyFile : String? = ""
    private var privateKeyFile : String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            backUpOnCloud = it.getBoolean(ARG_BU_AVAILABLE)
            publicKeyFile = it.getString(getString(R.string.ARG_PUB_KEY))
            privateKeyFile = it.getString(getString(R.string.ARG_PRIV_KEY))
            println("Llave publica: ${publicKeyFile}")
            println("Llave privada: ${privateKeyFile}")
        }

        //Creating directories for ciphered and deciphered data
        /*val cipherDataDirectory = File(this.requireContext().filesDir, "CipheredData")
        val decipheredDataDirectory = File(this.requireContext().filesDir, "DecipheredData")
        if(cipherDataDirectory.exists() || cipherDataDirectory.mkdir())
            cipheredDataPath = cipherDataDirectory.absolutePath
        if(decipheredDataDirectory.exists() || decipheredDataDirectory.mkdir())
            decipheredDataPath = decipheredDataDirectory.absolutePath*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStart() {
        super.onStart()
        setText()
        /*btnSelect.setOnClickListener { v: View? ->
            //Creating document picker
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivityForResult(intent, 42)
        }

        btnCifrar.setOnClickListener { v: View? ->
            val createBackup = Backup(this.requireContext().applicationContext, this.requireContext().filesDir.absolutePath)
            createBackup.readConfiguration()
            createBackup.testConfigFile()
            createBackup.readAll(cipheredDataPath!!)
        }

        btnDescifrar.setOnClickListener { v : View? ->
            val decipher = DescifradorAES_CFB(this.requireContext(), publicKeyFile!!, privateKeyFile!!)
            decipher.recoverKeys()
            val cipheredFiles = File(cipheredDataPath!!)
            readDirectory(cipheredFiles, decipheredDataPath!!, decipher)
        }*/
    }

    private fun readDirectory(f: File, decipheredDataPath: String, descifrador: DescifradorAES_CFB){
        if(f.isDirectory){
            f.listFiles()?.forEach { documentFile ->
                if(documentFile.isDirectory) {
                    File(decipheredDataPath, documentFile.name).mkdir()
                    readDirectory(documentFile, "$decipheredDataPath/${documentFile.name}", descifrador)
                }
                else descifrador.decipherFile(documentFile.absolutePath, decipheredDataPath, documentFile.name)
            }
        }
    }

    private fun setText() {
        if(backUpOnCloud){
            text_home.text = "Hay un respaldo en la nube"
        }else{
            text_home.text = "No hay un respaldo pendiente\n Hurray?"
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 42 && resultCode == Activity.RESULT_OK) {
            data?.data.also { uri ->
                uri?.let {
                    val newBackup = Backup(this.requireContext().applicationContext, this.requireContext().filesDir.absolutePath)
                    if (newBackup.existConfiguration())
                        newBackup.modifyConfiguration(it)
                    else
                        newBackup.createConfiguration(it)
                }
            }
        }
    }*/

    companion object {
        @JvmStatic
        fun newInstance(backUpOnCloud: Boolean, privateKeyFile: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_BU_AVAILABLE, backUpOnCloud)
                    //putString(getString(R.string.ARG_PUB_KEY), publicKeyFile)
                    putString(getString(R.string.ARG_PRIV_KEY), privateKeyFile)
                }
            }
    }


}