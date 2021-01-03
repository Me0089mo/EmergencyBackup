package com.example.emergencybackupv10

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.util.*

class Backup(val applicationContext : Context, val destiny:String) {

    private val configFileName = "configFile.txt"
    var selectDir : List<String> = mutableListOf()

    fun createConfiguration(treeUri : Uri){
        val configFile = File(destiny, configFileName)
        if(configFile.createNewFile())
            configFile.writeText(treeUri.toString()+"\n")
    }

    fun modifyConfiguration(treeUri: Uri){
        val configFile = File(destiny, configFileName)
        configFile.appendText(treeUri.toString()+"\n")
    }

    fun existConfiguration(): Boolean {
        return File(destiny, configFileName).exists()
    }

    fun readConfiguration() {
        val configFile = File("$destiny/$configFileName")
        selectDir = configFile.readLines()
    }

    fun testConfigFile(){
        selectDir.forEach { f -> println(f) }
    }

    fun readAll(backupDestiny : String){
        println("Tiempo de inicio: ${Calendar.getInstance().timeInMillis}")
        val cifrador = CifradorAES_CFB(applicationContext)
        selectDir.forEach { dir ->
            val docFile = DocumentFile.fromTreeUri(applicationContext, Uri.parse(dir))
            val rootDir = "$backupDestiny/${docFile?.name}"
            File(rootDir).mkdir()
            readDirectory(docFile!!, rootDir, cifrador)
        }
        println("Tiempo final: ${Calendar.getInstance().timeInMillis}")
    }

    private fun readDirectory(f:DocumentFile, cipheredDataPath:String, cifrador:CifradorAES_CFB){
        if(f.isDirectory){
            f.listFiles().forEach { documentFile ->
                if(documentFile.isDirectory) {
                    File(cipheredDataPath, documentFile.name).mkdir()
                    readDirectory(documentFile, "$cipheredDataPath/${documentFile.name}", cifrador)
                }
                else cifrador.cipherFile(documentFile.uri, cipheredDataPath, documentFile.name!!)
            }
        }
    }
}