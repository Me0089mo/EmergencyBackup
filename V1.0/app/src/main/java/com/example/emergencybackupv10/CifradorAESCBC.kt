package com.example.emergencybackupv10

import android.content.Context
import android.net.Uri
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class CifradorAESCBC(val contentContext: Context) {

    private val cipher:Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    private val keyGenerator = KeyGenerator.getInstance("AES")
    private var cipheredFile:File? = null
    private var cipheredOutput:CipherOutputStream? = null

    fun readFile(path : Uri, cipheredDataPath : String, fileName : String){
        createDestinationFile(cipheredDataPath, fileName)
        contentContext.contentResolver.openInputStream(path)?.use { reader ->
            val inputStream = BufferedInputStream(reader)
            val byteOutStream = ByteArrayOutputStream()
            val zipOutStream = ZipOutputStream(byteOutStream)
            val zipEntry = ZipEntry(path.path)
            zipOutStream.putNextEntry(zipEntry)
            val readingArray = ByteArray(10240)
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

    private fun initializeCipher(){
        keyGenerator.init(128)
        val key: SecretKey = keyGenerator.generateKey()
        cipher.init(Cipher.ENCRYPT_MODE, key)
    }

    private fun cipherData(data : ByteArray){
        cipheredOutput?.write(data)
    }

    private fun finalizeCipher(){
        cipheredOutput?.close()
    }

    private fun createDestinationFile(path : String, fileName : String){
        cipheredFile = File("$path/$fileName")
        val fos = FileOutputStream(cipheredFile)
        cipheredOutput = CipherOutputStream(fos, cipher)
    }

}