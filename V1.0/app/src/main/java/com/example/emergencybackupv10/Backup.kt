package com.example.emergencybackupv10

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import java.io.File

class Backup(val applicationContext : Context, val destiny:String) {

    private val configFileName = "configFile.txt"
    var selectDir : List<String> = mutableListOf()

    fun createConfiguration(treeUri : Uri){
        val configFile = File(destiny, configFileName)
        if(configFile.createNewFile()) {
            configFile.writeText(treeUri.toString())
        }
    }

    fun readConfiguration(){
        val configFile = File("$destiny/$configFileName")
        selectDir = configFile.readLines()
    }

    fun readAll(){
        val cifrador = CifradorAESCBC(applicationContext)
        selectDir.forEach { dir ->
            val docFile = DocumentFile.fromTreeUri(applicationContext, Uri.parse(dir))
            println(docFile?.name)
            readDirectory(docFile!!, destiny, cifrador)
        }
    }

    private fun readDirectory(f:DocumentFile, cipheredDataPath:String, cifrador:CifradorAESCBC){
        if(f.isDirectory){
            f.listFiles().forEach { documentFile ->
                println(documentFile.name)
                if(documentFile.isDirectory) {
                    File(cipheredDataPath, documentFile.name).mkdir()
                    readDirectory(documentFile, "$cipheredDataPath/${documentFile.name}", cifrador)
                }
                else cifrador.readFile(documentFile.uri, cipheredDataPath, documentFile.name!!)
            }
        }
    }

}