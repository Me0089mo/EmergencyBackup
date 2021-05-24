package com.example.emergencybackupv10

import android.content.Context
import android.net.Uri
import com.example.emergencybackupv10.utils.AlertUtils
import java.io.*
import java.security.*
import java.util.zip.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/*
    IV size = 16 bytes
    Key size = 16 bytes
    Tag size = 32 bytes
    Total = 64
 */
class DescifradorAES_CFB(val applicationContext : Context, val pkDirectory: Uri?): CipherFactory() {
    override val cipher:Cipher = Cipher.getInstance("AES/CFB/PKCS5PADDING")
    override val keyCipher = Cipher.getInstance("RSA/ECB/OAEPPADDING")
    override val keyManager = KeyManager(applicationContext)
    override val compressor = Compressor()
    override lateinit var cipheredOutput : CipherOutputStream
    override lateinit var fileOutStream : FileOutputStream
    override lateinit var byteOutStream : ByteArrayOutputStream
    override lateinit var mac : HMAC
    private lateinit var decipheredFileOutStream : FileOutputStream
    private lateinit var zipInputStream: ZipInputStream
    private lateinit var decipheredFile : File
    private lateinit var decompressedFile : File
    private lateinit var macTag : ByteArray
    private lateinit var key : SecretKey
    private var userPrivateKey : PrivateKey
    private var macMatches = false
    private val decipheredDataPath: String = applicationContext.filesDir.absolutePath + "/DecipheredData"

    init {
        userPrivateKey = pkDirectory?.let { keyManager.recoverPrivateKey(it) }!!
        File(decipheredDataPath).mkdir()
    }

    override fun processFile(path: Uri, fileName: String) {
        createOutputFile(decipheredDataPath, fileName)
        applicationContext.contentResolver.openInputStream(path)?.use { reader ->
            val fileInput = BufferedInputStream(reader)

            //Reading key mac and tag
            val cipheredKeyMac = ByteArray(128)
            macTag = ByteArray(32)
            fileInput.read(macTag)
            fileInput.read(cipheredKeyMac)
            //Reading key and IV
            val cipheredKey = ByteArray(128)
            val iv = ByteArray(16)
            fileInput.read(cipheredKey)
            fileInput.read(iv)

            initializeCipher(cipheredKey, iv, cipheredKeyMac)

            val readingArray = ByteArray(1024)
            var readLong:Int
            do{
                readLong = fileInput.read(readingArray)
                if(readLong == -1) break
                processData(readingArray, readLong)
            }while(readLong >= 0)
            finalizeCipher()
            if(macMatches) decompressData()
            else{
                AlertUtils().topToast(applicationContext, "Archivo $fileName corrupto. No se restaurará y será eliminado")
                decipheredFile.delete()
                decompressedFile.delete()
            }
            println(macMatches)
        }
    }

    override fun processData(data: ByteArray, numBytes: Int) {
        cipheredOutput.write(data, 0, numBytes)
        mac.calculateMac(data, numBytes)
    }

    override fun createOutputFile(path: String, fileName: String) {
        decompressedFile = File("$path/$fileName")
        decipheredFile = File("$path/${generateNewName(fileName)}")
        decipheredFileOutStream = FileOutputStream(decipheredFile)
        byteOutStream = ByteArrayOutputStream(1024)
        fileOutStream = FileOutputStream(decompressedFile)
        cipheredOutput = CipherOutputStream(decipheredFileOutStream, cipher)
    }

    override fun processKey(cipherKey: Key, data: ByteArray): ByteArray {
        keyCipher.init(Cipher.DECRYPT_MODE, cipherKey)
        return keyCipher.doFinal(data)
    }

    private fun initializeCipher(cipheredKey : ByteArray, iv : ByteArray, cipheredKeyMac: ByteArray){
        key = SecretKeySpec(processKey(userPrivateKey, cipheredKey), "AES")
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        val keyMac = SecretKeySpec(processKey(userPrivateKey, cipheredKeyMac), "HMAC")
        mac = HMAC(keyMac)
        mac.initializeMac()
    }

    private fun finalizeCipher(){
        cipheredOutput.close()
        decipheredFileOutStream.close()
        mac.finalizeMac()
        if(mac.verifyMac(macTag))
            macMatches = true
    }

    private fun decompressData(){
        zipInputStream = ZipInputStream(FileInputStream(decipheredFile))
        var readingArray = ByteArray(1024)
        var reading = 0
        if(zipInputStream.nextEntry != null){
            while(reading != -1){
                reading = zipInputStream.read(readingArray)
                if(reading != -1)
                    fileOutStream.write(readingArray, 0, reading)
            }
            fileOutStream.close()
            zipInputStream.closeEntry()
        }
        zipInputStream.close()
        decipheredFile.delete()
    }

    private fun generateNewName(name:String):String {
        var newName = ""
        for (index in (name.length - 1).downTo(0)) {
            if (name[index] == '.') {
                newName = name.substring(0, index) + "Deciphered"
                newName += name.substring(index, name.length)
                break
            }
        }
        return newName
    }
}