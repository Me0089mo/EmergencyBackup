
package com.example.emergencybackupv10

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*

class Backup(val applicationContext : Context, val dirList:MutableSet<String>, val cipherFactory: CipherFactory) {

    private val now = Calendar.getInstance().timeInMillis
    private var directories: MutableList<String> = mutableListOf()

    fun start(){
        if(cipherFactory.isCipher())
            getValuesFromDirList()
        else
            directories = dirList.toMutableList()
        directories.forEach { dir ->
            var docFile: DocumentFile?
            try {
                docFile = DocumentFile.fromTreeUri(applicationContext, Uri.parse(dir))
            }catch(exep: IllegalArgumentException){
                docFile = DocumentFile.fromFile(File(dir))
            }
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
                println("Restoring File: ${file.name}")
                //println("Last modified: ${file.lastModified()}")
                //println("Resta: ${now-file.lastModified()}")
                cipherFactory.processFile(file.uri, file.name!!)
            }
        }
    }

    fun getValuesFromDirList(){
        var directories: MutableList<Pair<Int, String>> = mutableListOf()
        for (dir in dirList) {
            var priority = dir.dropLastWhile { it != '|' }
            val onlyUri = dir.dropWhile { it != '|' }.substring(1)
            priority = priority.substring(0, priority.length-1)
            directories.add(Pair(priority.toInt(), onlyUri))
        }
        directories.sortBy { it.first }
        directories.forEach { dir ->
            this.directories.add(dir.second)
        }
    }
}