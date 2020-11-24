package com.example.pruebascifrado

import android.content.Context
import android.net.Uri
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class DESede_CBC(override val contentContext: Context):Cifrador {

    override val cipher:Cipher = Cipher.getInstance("DESEDE/CBC/PKCS5PADDING")
    override val keyGenerator = KeyGenerator.getInstance("DESEDE")
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
            initializeCipher(fileSize)
            do{
                long = inputStream.read(readingArray)
                if(long == -1) break
                zipOutStream.write(readingArray, 0, long)
                cipherData(byteOutStream.toByteArray(), cipherOffset, long)
                cipherOffset += long
                byteOutStream.reset()
            }while (long >= 0)
            finalizeCipher(cipherOffset)
            zipOutStream.close()
            byteOutStream.close()
        }
    }

    override fun initializeCipher(fileSize : Long){
        keyGenerator.init(64)
        val key: SecretKey = keyGenerator.generateKey()
        cipher.init(Cipher.ENCRYPT_MODE, key)
        cipherText = ByteArray(cipher.getOutputSize(fileSize.toInt()))
    }

    override fun cipherData(data : ByteArray, cipherOffset : Int, inputLen : Int){
        //println("Available: ${getAvailableMemory().availMem}\tThreshold: ${getAvailableMemory().threshold}")
        cipher.update(data, 0, inputLen, cipherText, cipherOffset)
    }

    override fun finalizeCipher(cipherOffset: Int){
        cipher.doFinal(cipherText, cipherOffset)
        //val iv: ByteArray = cipher.iv
    }
}