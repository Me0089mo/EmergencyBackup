package com.example.pruebascifrado

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import java.io.*
import java.lang.ref.Reference
import java.security.*

class MainActivity : AppCompatActivity() {

    private var cipheredDataPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*Security.getAlgorithms("Cipher").forEach {alg :String ->
            if(alg.startsWith("AES") || alg.startsWith("DESEDE"))
                println(alg)
        }*/

        //Creating directory for ciphered data
       val cipherDataDirectory = File(applicationContext.filesDir, "CipheredData")
        if(cipherDataDirectory.mkdir())
            //println("Directory correctly created")
            cipheredDataPath = cipherDataDirectory.absolutePath
        //println("Data path: $cipheredDataPath")


        //Creating document picker
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivityForResult(intent, 42)
    }

    @SuppressLint("LongLogTag")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 42 && resultCode == Activity.RESULT_OK) {
            data?.data.also { uri ->
                uri?.let {
                    val f = DocumentFile.fromTreeUri(this, it)
                    if(f != null){
                        Log.d("Started AES_CBC free storage: ", "${applicationContext.filesDir.freeSpace}")
                        File(cipheredDataPath, "AES CBC").mkdir()
                        readCipherDirectory(f, "$cipheredDataPath/AES CBC", "AESCBC")
                        Log.d("Ended AES_CBC free storage: ", "${applicationContext.filesDir.freeSpace}")
                        Log.d("Started AES_CTR free storage: ", "${applicationContext.filesDir.freeSpace}")
                        File(cipheredDataPath, "AES CTR").mkdir()
                        readCipherDirectory(f, "$cipheredDataPath/AES CTR", "AESCTR")
                        Log.d("Ended AES_CTR free storage: ", "${applicationContext.filesDir.freeSpace}")
                        Log.d("Started AES_GCM free storage: ", "${applicationContext.filesDir.freeSpace}")
                        File(cipheredDataPath, "AES GCM").mkdir()
                        readCipherDirectory(f, "$cipheredDataPath/AES GCM", "AESGCM")
                        Log.d("Ended AES_GCM free storage: ", "${applicationContext.filesDir.freeSpace}")
                        Log.d("Started DESede_CBC free storage: ", "${applicationContext.filesDir.freeSpace}")
                        File(cipheredDataPath, "DESede CBC").mkdir()
                        readCipherDirectory(f, "$cipheredDataPath/DESede CBCB", "DESCBC")
                    }
                }
            }
        }
    }

    /*fun analyzeDirectory(uri:Uri){
        val cursor: Cursor? = applicationContext.contentResolver.query(uri, null, null, null, null, null)
        cursor?.use {
            println("Analizing directory")
            if(it.moveToFirst()){
                val name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                println(name)
            }
        }
    }*/

    fun readCipherDirectory(f:DocumentFile, cipheredDataPath:String, algoritmo:String){
        if(f.isDirectory!!){
            f.listFiles().forEach { documentFile ->
                //println(documentFile.name)
                if(documentFile.isDirectory) {
                    File(cipheredDataPath, documentFile.name).mkdir()
                    readCipherDirectory(documentFile, "$cipheredDataPath/${documentFile.name}", algoritmo)
                }
                else {
                    var auxNewName: String? = documentFile.name
                    var cipheredName: String = ""
                    if (auxNewName != null)
                        cipheredName = generateNewName(auxNewName)
                    var cifrador:Cifrador?
                    if(algoritmo.contentEquals("AESCBC"))
                        cifrador = AES128_CBC(documentFile.uri, applicationContext)
                    else if(algoritmo.contentEquals("AESCTR"))
                        cifrador = AES128_CTR(documentFile.uri, applicationContext)
                    else if(algoritmo.contentEquals("AESGCM"))
                        cifrador = AES128_GCM(documentFile.uri, applicationContext)
                    else
                        cifrador = DESede_CBC(documentFile.uri, applicationContext)
                    cifrador.readFile()
                    val cipheredData = cifrador.cipherData()
                    var cipheredFile = File("$cipheredDataPath/$cipheredName")
                    cipheredFile.writeBytes(cipheredData)
                }
            }
        }
    }

    fun generateNewName(name:String):String{
        var newName = ""
        for (index in (name.length-1).downTo(0)){
            if (name[index] == '.'){
                newName = name.substring(0, index) + "Ciphered"
                newName += name.substring(index, name.length)
                break
            }
        }
        return newName
    }
}