package com.example.pruebascifrado

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.core.net.toUri
import java.io.*
import java.net.URI
import java.security.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class MainActivity : AppCompatActivity() {

    private var fileData: ByteArray? = null
    private var encryptedData:ByteArray? = null
    private var decryptedData:ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*Security.getAlgorithms("Cipher").forEach {alg :String ->
            println(alg)
        }*/
        println("Started")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            //type = "*/*"
        }
        startActivityForResult(intent, 42)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 42 && resultCode == Activity.RESULT_OK) {
            println(data?.data)
            val path:Uri = DocumentsContract.buildDocumentUri(data?.data?.authority, data?.data?.lastPathSegment)
            println(path.normalizeScheme())
            readFile(path)
            println("Final path: ${path}")
            println(path.lastPathSegment)
            println(URI.create(path.toString()))
            /*val direct = File(URI.create(path.toString()))
            println(direct.name)
            println("Archivo: ${direct?.isFile}")
            println("Directorio: ${direct?.isDirectory}")
            if(direct!!.isDirectory){
                println("Name: ${direct?.name}")
                val allFiles = direct?.listFiles()
                allFiles.forEachIndexed { index, file ->
                    println("$index: ${file.name}")
                    readFile(file.toUri())
                }

                /*fileData?.forEach { b:Byte->
                    print(b)
                }
                encryptedData = if(fileData != null) cipherFile(fileData!!) else null
                println("\n")
                encryptedData?.forEach { b: Byte ->
                    print(b)
                }*/
            }*/
        }
    }

    @Throws(IOException::class)
    fun readFile(path: Uri): ByteArray? {
        var data: ByteArray? = null
        contentResolver.openInputStream(path)?.use { reader ->
            val byte_arr = BufferedInputStream(reader).readBytes()
            data = ByteArray(byte_arr.size)
            data = byte_arr.clone()
            println("\tSize: ${byte_arr.size}")
            /* Opción de Android Developers
            var nextByte: Int = inputStream.read()
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte)
                nextByte = inputStream.read()
            }*/
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
}

/*val plaintext: ByteArray =
        val keygen = KeyGenerator.getInstance("AES")
        keygen.init(256)
        val key: SecretKey = keygen.generateKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val ciphertext: ByteArray = cipher.doFinal(plaintext)
        val iv: ByteArray = cipher.iv*/
