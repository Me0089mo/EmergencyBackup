package com.example.emergencybackupv10

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.*
import java.security.*
import java.security.spec.X509EncodedKeySpec
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.crypto.*

class AEScfbCipher(val applicationContext: Context) : CipherFactory(){

    override val cipher:Cipher = Cipher.getInstance("AES/CFB/PKCS5PADDING")
    override val keyCipher = Cipher.getInstance("RSA/ECB/OAEPPadding")
    override val keyManager = KeyManager(applicationContext)
    override val compressor = Compressor()
    override val cipherOrDecipher: Boolean = true
    override lateinit var cipheredOutput : CipherOutputStream
    override lateinit var fileOutStream : FileOutputStream
    override lateinit var byteOutStream: ByteArrayOutputStream
    override lateinit var mac : HMAC
    private val keyGenerator = KeyGenerator.getInstance("AES")
    private val cipheredDataPath: String = applicationContext.filesDir.absolutePath + "/CipheredData"
    //Para pruebas
    //private val cipheredDataPath: String = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.absolutePath + "/CipheredData"
    private var userPublicKey : PublicKey
    private var serverPublicKey : PublicKey


    init {
        keyGenerator.init(128)
        val pKeys = keyManager.recoverPublicKeys()
        userPublicKey = pKeys[0]
        serverPublicKey = pKeys[1]
        File(cipheredDataPath).mkdir()
    }

    override fun processFile(path : Uri, fileName : String){
        createOutputFile(cipheredDataPath, fileName)
        applicationContext.contentResolver.openInputStream(path)?.use { reader ->
            val inputStream = BufferedInputStream(reader)
            val readingArray = ByteArray(1024)
            val entry = compressor.newFile(fileName)
            var dataRead:Int
            var compressedData:ByteArray

            initializeCipher()
            //Writing the new entry
            processData(entry, entry.size)
            do{
                dataRead = inputStream.read(readingArray)
                if(dataRead == -1) break
                compressedData = compressor.compressData(readingArray, dataRead)
                processData(compressedData, compressedData.size)
            }while(dataRead >= 0)
            val entryClose = compressor.finalizeCompression()
            processData(entryClose, entryClose.size)
            finalizeCipher()
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

    override fun processData(data: ByteArray, numBytes: Int) {
        cipheredOutput.write(data, 0, numBytes)
        val aux = byteOutStream.toByteArray()
        fileOutStream.write(aux)
        mac.calculateMac(aux, aux.size)
        byteOutStream.reset()
    }

    private fun finalizeCipher(){
        cipheredOutput.close()
        val aux = byteOutStream.toByteArray()
        fileOutStream.write(aux)
        mac.calculateMac(aux, aux.size)
        mac.finalizeMac()
        fileOutStream.channel.position(0)
        fileOutStream.write(mac.tag)
        fileOutStream.write(processKey(serverPublicKey, mac.key.encoded))
        fileOutStream.close()
    }

    override fun createOutputFile(path : String, fileName : String){
        val cipheredFile = File("$path/${fileName}")
        fileOutStream = FileOutputStream(cipheredFile)
        fileOutStream.channel.position(160)
        byteOutStream = ByteArrayOutputStream()
        cipheredOutput = CipherOutputStream(byteOutStream, cipher)
    }

    override fun processKey(cipherKey : Key, data : ByteArray) : ByteArray?{
        keyCipher.init(Cipher.ENCRYPT_MODE, cipherKey)
        val ciphKey = keyCipher.doFinal(data)
        //println("Ciphered MAC ")
        //ciphKey.forEach { b -> print("$b ") }
        return ciphKey
    }

    fun print_bytes(bytes: ByteArray): String? {
        val sb = StringBuilder()
        sb.append("[ ")
        for (b in bytes) {
            sb.append(String.format("0x%02X ", b))
        }
        sb.append("]")
        return sb.toString()
    }

    private fun generateNewName(name:String):String {
        var newName = ""
        for (index in (name.length - 1).downTo(0)) {
            if (name[index] == '.') {
                newName = name.substring(0, index) + "Ciphered"
                newName += name.substring(index, name.length)
                break
            }
        }
        return newName
    }

    override fun isCipher(): Boolean = cipherOrDecipher

}