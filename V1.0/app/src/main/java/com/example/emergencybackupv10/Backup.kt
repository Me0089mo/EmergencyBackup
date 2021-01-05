

package com.example.emergencybackupv10

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.util.*

class Backup(val applicationContext : Context, val dirList:MutableSet<String>) {
    private val cifrador = CifradorAES_CFB(applicationContext)

    fun create(){
        dirList.forEach { dir ->
            val docFile = DocumentFile.fromTreeUri(applicationContext, Uri.parse(dir))
            if (docFile!!.exists() && docFile.isDirectory){
                encryptDir(docFile)
            }
        }
        println("Tiempo final: ${Calendar.getInstance().timeInMillis}")
    }

    private fun encryptDir(directory:DocumentFile){
            directory.listFiles().forEach { file ->
                if(file.isDirectory) {
                    File(applicationContext.filesDir, file.name!!).mkdir()
                    encryptDir(file)
                } else {
                    cifrador.cipherFile(file.uri, file.name!!)
                }
            }
    }
}