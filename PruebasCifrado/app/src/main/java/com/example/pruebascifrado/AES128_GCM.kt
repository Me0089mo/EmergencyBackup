package com.example.pruebascifrado

import android.content.Context
import android.net.Uri
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class AES128_GCM(override val contentContext: Context):Cifrador {

    override val cipher:Cipher = Cipher.getInstance("AES/CFB/PKCS5PADDING")
    override val keyGenerator = KeyGenerator.getInstance("AES")
    override var data:ByteArray? = null
    override var cipherText:ByteArray? = null
    var cipheredFile:File? = null
    var cipheredOutput: CipherOutputStream? = null

    override fun readFile(path : Uri, cipheredDataPath : String, fileName : String){
        createDestinationFile(cipheredDataPath, fileName)
        contentContext.contentResolver.openInputStream(path)?.use { reader ->
            val inputStream = BufferedInputStream(reader)
            val byteOutStream = ByteArrayOutputStream()
            val zipOutStream = ZipOutputStream(byteOutStream)
            val zipEntry = ZipEntry(path.path)
            zipOutStream.putNextEntry(zipEntry)
            val readingArray = ByteArray(1024)
            var readLong:Int
            var byteOutStreamConv:ByteArray
            var cipherOffset = 0

            initializeCipher()
            do{
                readLong = inputStream.read(readingArray)
                if(readLong == -1) break
                zipOutStream.write(readingArray, 0, readLong)
                byteOutStreamConv = byteOutStream.toByteArray()
                if(byteOutStreamConv.isNotEmpty()) {
                    cipherData(byteOutStreamConv)
                    cipherOffset += byteOutStreamConv.size
                }
                byteOutStream.reset()
            }while(readLong >= 0)
            finalizeCipher()

            zipOutStream.close()
            byteOutStream.close()
        }
    }

    override fun initializeCipher(){
        keyGenerator.init(128)
        val key : SecretKey = keyGenerator.generateKey()
        cipher.init(Cipher.ENCRYPT_MODE, key)
    }

    override fun cipherData(data : ByteArray){
        cipheredOutput?.write(data)
        cipheredOutput?.flush()
    }

    override fun finalizeCipher(){
        cipheredOutput?.close()
    }

    fun createDestinationFile(path : String, fileName : String){
        cipheredFile = File("$path/$fileName")
        val fos = FileOutputStream(cipheredFile)
        cipheredOutput = CipherOutputStream(fos, cipher)
    }
}