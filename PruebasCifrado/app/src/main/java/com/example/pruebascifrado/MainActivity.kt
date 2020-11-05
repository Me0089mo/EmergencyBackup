package com.example.pruebascifrado

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.M
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultRegistry
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import java.io.*
import java.net.URI
import java.security.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class MainActivity : AppCompatActivity() {

    /*private var fileData: ByteArray? = null
    private var encryptedData: ByteArray? = null
    private var decryptedData: ByteArray? = null*/
    private var cipheredDataPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*Security.getAlgorithms("Cipher").forEach {alg :String ->
            println(alg)
        }*/

        //Creating file for ciphered data
        val uri = Uri.parse(cipheredDataPath)
        val f = File(Environment.getDataDirectory().name + "/CipheredData")
        if(f.mkdir()) println("Correctly created")
        cipheredDataPath = f.toUri().toString()
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
                    if(f?.isDirectory!!){
                        f.listFiles().forEach { documentFile ->
                            var auxNewName: String? = documentFile.name
                            var cipheredName:String = ""
                            if(auxNewName != null)
                                cipheredName = generateNewName(auxNewName)
                            var plainData = readFile(documentFile.uri)
                            var cipherData = plainData?.let { it1 -> cipherFile(it1) }
                            var cipheredFile = File(cipheredDataPath + "/" + cipheredName)
                            cipheredFile.mkdir()
                            if (cipherData != null) {
                                cipheredFile.writeBytes(cipherData)
                            }
                        }
                    }
                }
            }
        }
    }

    fun analyzeDirectory(uri:Uri){
        val cursor: Cursor? = applicationContext.contentResolver.query(uri, null, null, null, null, null)
        cursor?.use {
            println("Analizing directory")
            if(it.moveToFirst()){
                val name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                println(name)
            }
        }
    }

    @Throws(IOException::class)
    fun readFile(path: Uri): ByteArray? {
        var data: ByteArray? = null
        println(path.path)
        contentResolver.openInputStream(path)?.use { reader ->
            val byte_arr = BufferedInputStream(reader).readBytes()
            data = ByteArray(byte_arr.size)
            data = byte_arr.clone()
            println("\tSize: ${byte_arr.size}")
        }
        return data
    }

    fun cipherFile(data:ByteArray):ByteArray{
        val keygen = KeyGenerator.getInstance("AES")
        keygen.init(256)
        val key: SecretKey = keygen.generateKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val ciphertext: ByteArray = cipher.doFinal(data)
        val iv: ByteArray = cipher.iv
        println(iv)
        return ciphertext
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

/*val plaintext: ByteArray =
        val keygen = KeyGenerator.getInstance("AES")
        keygen.init(256)
        val key: SecretKey = keygen.generateKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val ciphertext: ByteArray = cipher.doFinal(plaintext)
        val iv: ByteArray = cipher.iv*/
