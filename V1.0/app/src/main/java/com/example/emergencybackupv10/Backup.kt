
package com.example.emergencybackupv10

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.util.*

class Backup(val applicationContext : Context, val dirList:MutableSet<String>, val cipherFactory: CipherFactory) {

    private val now = Calendar.getInstance().timeInMillis

    fun start(){

        dirList.forEach { dir ->
            val docFile = DocumentFile.fromTreeUri(applicationContext, Uri.parse(dir))
            if (docFile!!.exists() && docFile.isDirectory){
                encryptDir(docFile)
            }
        }
        println("Current time: $now")
        //println("Tiempo final: ${Calendar.getInstance().timeInMillis}")
    }

    private fun encryptDir(directory:DocumentFile){
            directory.listFiles().forEach { file ->
                if(file.isDirectory) {
                    File(applicationContext.filesDir, file.name!!).mkdir()
                    encryptDir(file)
                } else {
                    println("Last modified: ${file.lastModified()}")
                    println("Resta: ${now-file.lastModified()}")
                    cipherFactory.processFile(file.uri, file.name!!)
                }
            }
    }
}