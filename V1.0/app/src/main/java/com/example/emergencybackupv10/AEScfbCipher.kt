package com.example.emergencybackupv10

import android.content.Context
import android.net.Uri
import java.io.*
import java.security.*
import java.security.spec.X509EncodedKeySpec
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.crypto.*

class AEScfbCipher(val applicationContext: Context) : CipherFactory(){

    override val cipher:Cipher = Cipher.getInstance("AES/CFB/PKCS5PADDING")
    override val keyCipher = Cipher.getInstance("RSA/ECB/OAEPPADDING")
    override val keyManager = KeyManager(applicationContext)
    override val compressor = Compressor()
    override lateinit var cipheredOutput : CipherOutputStream
    override lateinit var fileOutStream : FileOutputStream
    override lateinit var byteOutStream: ByteArrayOutputStream
    override lateinit var mac : HMAC
    private val keyGenerator = KeyGenerator.getInstance("AES")
    private val cipheredDataPath: String = applicationContext.filesDir.absolutePath + "/CipheredData"
    private var userPublicKey : PublicKey
    private var serverPublicKey : PublicKey


    init {
        keyGenerator.init(128)
        val pKeys = keyManager.recoverPublicKeys()
        userPublicKey = pKeys[0]
        serverPublicKey = pKeys[1]
    }

    override fun processFile(path : Uri, fileName : String){
        createOutputFile(cipheredDataPath, fileName)
        applicationContext.contentResolver.openInputStream(path)?.use { reader ->
            val inputStream = BufferedInputStream(reader)
            val readingArray = ByteArray(1024)
            val entry = compressor.newFile(fileName)
            //Writing the new entry
            fileOutStream.write(entry)
            var dataRead:Int
            var compressedData:ByteArray
            initializeCipher()
            var band = 1
            do{
                dataRead = inputStream.read(readingArray)
                if(dataRead == -1) break
                compressedData = compressor.compressData(readingArray)
                if(compressedData.isNotEmpty()) {
                        if(band == 1){
                        compressedData.forEach { b -> print("$b ") }
                        print("\n")
                        band = 0
                    }
                    processData(compressedData)
                }
            }while(dataRead >= 0)
            finalizeCipher(compressor.finalizeCompression())
        }
    }

    private fun initializeCipher(){
        val key = keyGenerator.generateKey()
        cipher.init(Cipher.ENCRYPT_MODE, key)
        mac = HMAC()
        mac.initializeMac()
        fileOutStream.write(processKey(userPublicKey, key.encoded))
        fileOutStream.write(cipher.iv)
    }

    override fun processData(data : ByteArray){
        cipheredOutput.write(data)
        val aux = byteOutStream.toByteArray()
        fileOutStream.write(aux)
        mac.calculateMac(aux)
        byteOutStream.reset()
    }

    private fun finalizeCipher(zipEntryClosing  : ByteArray){
        fileOutStream.write(zipEntryClosing)
        cipheredOutput.close()
        /*fileOutStream.write(byteOutStream.toByteArray())
        mac.calculateMac(byteOutStream.toByteArray())*/
        mac.finalizeMac()
        fileOutStream.channel.position(0)
        fileOutStream.write(mac.tag)
        /*print("Original tag: ")
        mac.tag.forEach { b -> print("$b, ") }
        print("\nOriginal mac key: ")
        mac.key.encoded.forEach { b -> print("$b, ") }
        print("\nOriginal cipher key: ")
        key.encoded.forEach { b -> print("$b, ") }
        print("\nOriginal iv: ")
        cipher.iv.forEach { b -> print("$b, ") }
        print("\n")*/

        fileOutStream.write(processKey(serverPublicKey, mac.key.encoded))
        fileOutStream.close()
    }

    override fun createOutputFile(path : String, fileName : String){
        val cipheredFile = File("$path/$fileName")
        fileOutStream = FileOutputStream(cipheredFile)
        fileOutStream.channel.position(160)
        byteOutStream = ByteArrayOutputStream()
        cipheredOutput = CipherOutputStream(byteOutStream, cipher)
    }

    override fun processKey(cipherKey : Key, data : ByteArray) : ByteArray{
        keyCipher.init(Cipher.ENCRYPT_MODE, cipherKey)
        return keyCipher.doFinal(data)
    }
}