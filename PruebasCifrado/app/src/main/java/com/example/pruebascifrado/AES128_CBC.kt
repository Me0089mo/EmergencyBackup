package com.example.pruebascifrado

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toFile
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class AES128_CBC(override val contentContext: Context):Cifrador {

    override val cipher:Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    override val keyGenerator = KeyGenerator.getInstance("AES")
    override var data:ByteArray? = null
    override var cipherText:ByteArray? = null

    override fun readFile(path : Uri, fileSize : Long){
        contentContext.contentResolver.openInputStream(path)?.use { reader ->
            val inputStream = BufferedInputStream(reader)
            val byteOutStream = ByteArrayOutputStream()
            val zipOutStream = ZipOutputStream(byteOutStream)
            val zipEntry = ZipEntry(path.path)
            zipOutStream.putNextEntry(zipEntry)
            val readingArray = ByteArray(1024)
            var long:Int
            var cipherOffset = 0
            var byteOutStreamConv:ByteArray
            println("File size: $fileSize")
            initializeCipher(fileSize)
            do{
                long = inputStream.read(readingArray)
                if(long == -1) break
                zipOutStream.write(readingArray, 0, long)
                byteOutStreamConv = byteOutStream.toByteArray()
                if(byteOutStreamConv.isNotEmpty())
                    cipherData(byteOutStreamConv, cipherOffset, byteOutStreamConv.size)
                cipherOffset += byteOutStreamConv.size
                byteOutStream.reset()
            }while (long >= 0)
            finalizeCipher(cipherOffset)
            zipOutStream.close()
            byteOutStream.close()
        }
    }

    override fun initializeCipher(fileSize : Long){
        keyGenerator.init(128)
        val key: SecretKey = keyGenerator.generateKey()
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val outSize = cipher.getOutputSize(fileSize.toInt())
        cipherText = ByteArray(outSize)
    }

    override fun cipherData(data : ByteArray, cipherOffset : Int, inputLen : Int){
        cipher.update(data, 0, inputLen, cipherText, cipherOffset)
    }

    override fun finalizeCipher(cipherOffset: Int){
        cipher.doFinal(cipherText, cipherOffset)
        //val iv: ByteArray = cipher.iv
    }

    private fun getAvailableMemory(): ActivityManager.MemoryInfo {
        val activityManager = contentContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return ActivityManager.MemoryInfo().also { memoryInfo ->
            activityManager.getMemoryInfo(memoryInfo)
        }
    }
}