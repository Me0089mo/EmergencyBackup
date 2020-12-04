package com.example.pruebascifrado

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toFile
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class AES128_CBC(override val contentContext: Context):Cifrador {

    override val cipher:Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    override val keyGenerator = KeyGenerator.getInstance("AES")
    override var data:ByteArray? = null
    override var cipherText:ByteArray? = null
    var cipheredFile:File? = null
    var cipheredOutput:CipherOutputStream? = null

    override fun readFile(path : Uri, cipheredDataPath : String, fileName : String){
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

    override fun initializeCipher(){
        keyGenerator.init(128)
        val key: SecretKey = keyGenerator.generateKey()
        cipher.init(Cipher.ENCRYPT_MODE, key)
    }

    override fun cipherData(data : ByteArray){
        cipheredOutput?.write(data)
    }

    override fun finalizeCipher(){
        cipheredOutput?.close()
    }

    private fun createDestinationFile(path : String, fileName : String){
        cipheredFile = File("$path/$fileName")
        val fos = FileOutputStream(cipheredFile)
        cipheredOutput = CipherOutputStream(fos, cipher)
    }

    /*fun writeToFile(offset : Int){
        val fos = FileOutputStream(cipheredFile)
        fos.write(cipherText, 0, offset)
    }*/

    /*override fun readFile(path : Uri, fileSize : Long, cipheredDataPath : String, fileName : String){
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

            initializeCipher(fileSize)
            println("File size: $fileSize Out size: $outSize Array size: ${cipherText?.size}")
            do{
                readLong = inputStream.read(readingArray)
                if(readLong == -1) break
                zipOutStream.write(readingArray, 0, readLong)
                byteOutStreamConv = byteOutStream.toByteArray()
                if(byteOutStreamConv.isNotEmpty()) {
                    //println("Processed til: $cipherOffset")
                    var initialOffset = 0
                    if(cipherOffset + byteOutStreamConv.size >= maxProcessingSize){
                        initialOffset = cipherOffset % 128
                        if(cipherOffset % 128 != 0)
                            cipherData(byteOutStreamConv, cipherOffset, cipherOffset%128)
                        finalizeCipher(byteOutStreamConv, cipherOffset)
                        writeToFile(cipherOffset)
                        cipherOffset = 0
                        if(outSize >= maxProcessingSize){
                            cipherText = ByteArray(cipher.getOutputSize(maxProcessingSize))
                            outSize -= maxProcessingSize
                        }
                        else
                            cipherText = ByteArray(outSize + byteOutStreamConv.size - initialOffset)
                        println("File size: $fileSize Out size: $outSize Array size: ${cipherText?.size}")
                    }
                    if(initialOffset != 0) {
                        val auxiliarBuffer = byteOutStreamConv.copyOfRange(initialOffset, byteOutStreamConv.size)
                        cipherData(auxiliarBuffer, cipherOffset, auxiliarBuffer.size)
                        cipherOffset += auxiliarBuffer.size
                        initialOffset = 0
                    }
                    else {
                        cipherData(byteOutStreamConv, cipherOffset, byteOutStreamConv.size)
                        cipherOffset += byteOutStreamConv.size
                    }
                }
                /*if(cipherOffset >= maxProcessingSize){

                }
                byteOutStream.reset()*/
            }while(readLong >= 0)
            finalizeCipher(readingArray, cipherOffset)
            writeToFile(cipherOffset)
            zipOutStream.close()
            byteOutStream.close()
        }
    }*/
}