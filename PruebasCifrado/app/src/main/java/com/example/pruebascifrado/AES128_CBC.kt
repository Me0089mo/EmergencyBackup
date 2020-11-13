package com.example.pruebascifrado

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.BufferedInputStream
import java.io.File
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class AES128_CBC(override val path: Uri, override val contentContext: Context):Cifrador {

    override var data:ByteArray? = null

    override fun readFile(){
        contentContext.contentResolver.openInputStream(path)?.use { reader ->
            data = BufferedInputStream(reader).readBytes()
        }
    }

    override fun cipherData(): ByteArray{
        val keygen = KeyGenerator.getInstance("AES")
        keygen.init(128)
        val key: SecretKey = keygen.generateKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val ciphertext: ByteArray = cipher.doFinal(data)
        //val iv: ByteArray = cipher.iv
        return ciphertext
    }

    /*fun writeFile(data: ByteArray){
        var cipheredFile = File("$cipheredDataPath/$cipheredName")
        cipheredFile.writeBytes(data)
    }*/
}