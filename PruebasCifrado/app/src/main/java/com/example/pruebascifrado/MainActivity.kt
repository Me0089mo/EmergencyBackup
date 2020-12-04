package com.example.pruebascifrado

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import java.io.*

class MainActivity : AppCompatActivity() {

    private var cipheredDataPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Creating directory for ciphered data
        val cipherDataDirectory = File(applicationContext.filesDir, "CipheredData")
        if(cipherDataDirectory.mkdir())
            cipheredDataPath = cipherDataDirectory.absolutePath

        //Creating document picker
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivityForResult(intent, 42)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 42 && resultCode == Activity.RESULT_OK) {
            data?.data.also { uri ->
                uri?.let {
                    val f = DocumentFile.fromTreeUri(this, it)
                    if(f != null){
                        /*Log.d("Started AES_CBC free storage: ", "${applicationContext.filesDir.freeSpace}")
                        File(cipheredDataPath, "AES CBC").mkdir()
                        var aescbc = AES128_CBC(applicationContext)
                        readCipherDirectory(f, "$cipheredDataPath/AES CBC", aescbc)
                        Log.d("Ended AES_CBC free storage: ", "${applicationContext.filesDir.freeSpace}")
                        Log.d("Started AES_CTR free storage: ", "${applicationContext.filesDir.freeSpace}")
                        File(cipheredDataPath, "AES CTR").mkdir()
                        var aesctr = AES128_CTR(applicationContext)
                        readCipherDirectory(f, "$cipheredDataPath/AES CTR", aesctr)
                        Log.d("Ended AES_CTR free storage: ", "${applicationContext.filesDir.freeSpace}")
                        Log.d("Started AES_GCM free storage: ", "${applicationContext.filesDir.freeSpace}")*/
                        File(cipheredDataPath, "AES GCM").mkdir()
                        var aesgcm = AES128_GCM(applicationContext)
                        readCipherDirectory(f, "$cipheredDataPath/AES GCM", aesgcm)
                        Log.d("Ended AES_GCM free storage: ", "${applicationContext.filesDir.freeSpace}")
                        /*Log.d("Started DESede_CBC free storage: ", "${applicationContext.filesDir.freeSpace}")
                        File(cipheredDataPath, "DESede CBC").mkdir()
                        cifrador = AES128_CBC(applicationContext)
                        readCipherDirectory(f, "$cipheredDataPath/DESede CBC", cifrador)
                        Log.d("Ended DESede_CBC free storage: ", "${applicationContext.filesDir.freeSpace}")*/
                    }
                }
            }
        }
    }

    fun readCipherDirectory(f:DocumentFile, cipheredDataPath:String, cifrador:Cifrador){
        if(f.isDirectory){
            f.listFiles().forEach { documentFile ->
                //println(documentFile.name)
                if(documentFile.isDirectory) {
                    File(cipheredDataPath, documentFile.name).mkdir()
                    readCipherDirectory(documentFile, "$cipheredDataPath/${documentFile.name}", cifrador)
                }
                else {
                    val cipheredName = generateNewName(documentFile.name!!)
                    println(documentFile.name)
                    cifrador.readFile(documentFile.uri, cipheredDataPath, documentFile.name!!)
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