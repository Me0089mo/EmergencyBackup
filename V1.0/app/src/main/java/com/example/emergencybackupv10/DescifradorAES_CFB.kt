package com.example.emergencybackupv10

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.emergencybackupv10.utils.AlertUtils
import java.io.*
import java.lang.Exception
import java.security.*
import java.util.zip.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/*
    IV size = 16 bytes
    Key size = 128 bytes
    Tag size = 32 bytes
 */
class DescifradorAES_CFB(val applicationContext : Context, val pkDirectory: Uri?): CipherFactory() {
    override val cipher:Cipher = Cipher.getInstance("AES/CFB/PKCS5PADDING")
    override val keyCipher = Cipher.getInstance("RSA/ECB/OAEPPadding")
    override val keyManager = KeyManager(applicationContext)
    override val compressor = Compressor()
    override val cipherOrDecipher: Boolean = false
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
    private var errorProcessingKeys: Boolean = false
    private val alertUtils = AlertUtils()
    private var macMatches = false
    override val outDataPath: String = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.absolutePath + "/DecipheredData"
    //override val outDataPath: String = applicationContext.filesDir.absolutePath + "/DecipheredData"

    init {
        userPrivateKey = pkDirectory?.let { keyManager.recoverPrivateKey(it) }!!
        File(outDataPath).mkdir()
    }

    override fun processFile(path: Uri, fileName: String, outPath: String?) {
        createOutputFile(outDataPath, fileName)
        errorProcessingKeys = false
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
            if(errorProcessingKeys){
                alertUtils.topToast(applicationContext, "Llaves de archivo corruptas")
                closeStreams()
                return
            }

            val readingArray = ByteArray(1024)
            var readLong:Int
            do{
                readLong = fileInput.read(readingArray)
                if(readLong == -1) break
                processData(readingArray, readLong)
            }while(readLong >= 0)
            finalizeCipher()
            if(macMatches)
                compressor.decompressFile(decipheredFile, fileOutStream)
            else{
                AlertUtils().topToast(applicationContext, "Archivo $fileName corrupto. No se restaurará y será eliminado")
                decipheredFile.delete()
                decompressedFile.delete()
            }
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

    private fun closeStreams(){
        decipheredFileOutStream.close()
        byteOutStream.close()
        fileOutStream.close()
        decompressedFile.delete()
        decipheredFile.delete()
    }

    override fun processKey(cipherKey: Key, data: ByteArray): ByteArray? {
        keyCipher.init(Cipher.DECRYPT_MODE, cipherKey)
        var decKey: ByteArray? = null
        try {
            decKey = keyCipher.doFinal(data)
        }catch (exep: Exception){}
        return decKey
    }

    private fun initializeCipher(cipheredKey : ByteArray, iv : ByteArray, cipheredKeyMac: ByteArray){
        val decKey = processKey(userPrivateKey, cipheredKey)
        if(decKey == null){
            errorProcessingKeys = true
            return
        }
        key = SecretKeySpec(decKey, "AES")
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        val decKeyMac = processKey(userPrivateKey, cipheredKeyMac)
        if(decKeyMac == null){
            errorProcessingKeys = true
            return
        }
        val keyMac = SecretKeySpec(decKeyMac, "HMAC")
        mac = HMAC(keyMac)
        mac.initializeMac()
    }

    private fun finalizeCipher(){
        cipheredOutput.close()
        decipheredFileOutStream.flush()
        decipheredFileOutStream.close()
        mac.finalizeMac()
        if(mac.verifyMac(macTag))
            macMatches = true
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

    override fun isCipher(): Boolean = cipherOrDecipher
}