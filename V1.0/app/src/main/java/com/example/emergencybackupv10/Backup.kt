
package com.example.emergencybackupv10

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*

class Backup(val applicationContext : Context,
             val dirList:MutableSet<String>,
             val cipherFactory: CipherFactory,
             var time: Long=0) {

    private val now = Calendar.getInstance().timeInMillis
    private var directories: MutableList<String> = mutableListOf()

    fun start(){
        if(cipherFactory.isCipher())
            getValuesFromDirList()
        else
            directories = dirList.toMutableList()
        Log.i("Dir size", directories.size.toString())
        directories.forEach { dir ->
            var docFile: DocumentFile?
            try {
                docFile = DocumentFile.fromTreeUri(applicationContext, Uri.parse(dir))
            }catch(exep: IllegalArgumentException){
                docFile = DocumentFile.fromFile(File(dir))
            }
            if (docFile!!.exists() && docFile.isDirectory){
                if(cipherFactory.isCipher()) {
                    if(time.compareTo(0) == 0) time = 86400000
                    if(now-time <= docFile.lastModified()) {
                        File(cipherFactory.outDataPath, docFile.name).mkdir()
                        encryptDir(docFile, "${cipherFactory.outDataPath}/${docFile.name}")
                    }
                }
                else {
                    decryptDir(docFile)
                }
            }
        }
        //println("Tiempo final: ${Calendar.getInstance().timeInMillis}")
    }

    private fun encryptDir(directory:DocumentFile, outPath: String){
        directory.listFiles().forEach { file ->
            if(file.isDirectory) {
                if (now-time <= file.lastModified()) {
                    File(outPath, file.name!!).mkdir()
                    encryptDir(file, "$outPath/${file.name}")
                }
            } else {
                if (now-time <= file.lastModified())
                    cipherFactory.processFile(file.uri, file.name!!, outPath)
            }
        }
    }

    private fun decryptDir(directory: DocumentFile){
        directory.listFiles().forEach { file ->
            if(file.isDirectory) {
                File(applicationContext.filesDir, file.name!!).mkdir()
                decryptDir(file)
            }
            else
                cipherFactory.processFile(file.uri, file.name!!)
        }
    }

    fun getValuesFromDirList(){
        var directories: MutableList<Pair<Int, String>> = mutableListOf()
        for (dir in dirList) {
            var priority = dir.dropLastWhile { it != '|' }
            priority = priority.substring(0, priority.length-1).dropLastWhile { it != '|' }
            var onlyUri = dir.dropWhile { it != '|' }.substring(1)
            val onlyName = onlyUri.dropWhile { it != '|' }.substring(1)
            onlyUri = onlyUri.substring(0, onlyUri.length-1-onlyName.length)
            priority = priority.substring(0, priority.length-1)
            directories.add(Pair(priority.toInt(), onlyUri))
        }
        directories.sortBy { it.first }
        directories.forEach { dir ->
            this.directories.add(dir.second)
        }
    }
}