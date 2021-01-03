package com.example.emergencybackupv10

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.*
import java.security.*
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.zip.*
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/*
    IV size = 16 bytes
    Key size = 128 bytes
    Tag size = 32 bytes
    128+32 = 160
 */
class DescifradorAES_CFB(val contentContext : Context, val pubkDirectory: String, val pkDirectory: String) {
    private val cipher:Cipher = Cipher.getInstance("AES/CFB/PKCS5PADDING")
    private val keyCipher = Cipher.getInstance("RSA/ECB/OAEPPADDING")
    private lateinit var cipheredFile : File
    private lateinit var cipheredOutput : CipherOutputStream
    private lateinit var fileOutStream : FileOutputStream
    private lateinit var byteOutStream : ByteArrayOutputStream
    private lateinit var inflaterOutStream : InflaterOutputStream
    private lateinit var mac : HMAC
    private lateinit var macTag : ByteArray
    private lateinit var userPrivateKey : PrivateKey
    private lateinit var key : SecretKey

    fun decipherFile(path : String, decipheredDataPath : String, fileName : String){
        createDestinationFile(decipheredDataPath, fileName)
        val fileInput = FileInputStream(path)
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
        val zipInputStream = ZipInputStream(fileInput)
        val entry = zipInputStream.nextEntry
        if(entry == null) println("Null Zip Entry")
        else println("Size: ${entry.compressedSize}, Name: ${entry.name}")

        val byteOutReadStream = ByteArrayOutputStream()
        val readingArray = ByteArray(10240)
        var readLong:Int
        var byteOutStreamConv:ByteArray
        var cipherOffset = 0

        initializeCipher(cipheredKey, iv, cipheredKeyMac)
        do{
            readLong = zipInputStream.read(readingArray)
            println("Read bytes: $readLong")
            if(readLong == -1) break
            if(readingArray.isNotEmpty()) {
                decipherData(readingArray)
                cipherOffset += readingArray.size
            }
            byteOutReadStream.reset()
        }while(readLong >= 0)
        finalizeCipher()

        //zipOutStream.close()
        byteOutReadStream.close()
    }

    private fun initializeCipher(cipheredKey : ByteArray, iv : ByteArray, cipheredKeyMac: ByteArray){
        key = SecretKeySpec(decipherKey(userPrivateKey, cipheredKey), "AES")
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        val keyMac = SecretKeySpec(decipherKey(userPrivateKey, cipheredKeyMac), "HMAC")
        print("Recovered tag: ")
        macTag.forEach { b -> print("$b, ") }
        print("\nRecovered mac key: ")
        keyMac.encoded.forEach { b -> print("$b, ") }
        print("\nRecovered cipher key: ")
        key.encoded.forEach { b -> print("$b, ") }
        print("\nRecovered iv: ")
        iv.forEach { b -> print("$b, ") }
        print("\n")
        mac = HMAC(keyMac)
        mac.initializeMac()
    }

    private fun decipherData(data : ByteArray){
        cipheredOutput.write(data)
        val aux = byteOutStream.toByteArray()
        mac.calculateMac(aux!!)
        decompressData(aux)
        byteOutStream.reset()
    }

    private fun finalizeCipher(){
        cipheredOutput.close()
        fileOutStream.write(byteOutStream.toByteArray())
        mac.calculateMac(byteOutStream.toByteArray())
        mac.finalizeMac()
        if(mac.verifyMac(macTag)){
            //se reestablece archivo
        }
        inflaterOutStream.close()
        fileOutStream.close()
    }

    private fun createDestinationFile(path : String, fileName : String){
        cipheredFile = File("$path/$fileName")
        fileOutStream = FileOutputStream(cipheredFile)
        inflaterOutStream = InflaterOutputStream(fileOutStream)
        byteOutStream = ByteArrayOutputStream()
        cipheredOutput = CipherOutputStream(byteOutStream, cipher)
    }

    private fun decompressData(compressData : ByteArray){
        println("Before compression: ${compressData.size}")
        inflaterOutStream.write(compressData)
    }

    private fun decipherKey(cipherKey : Key, data : ByteArray) : ByteArray{
        keyCipher.init(Cipher.DECRYPT_MODE, cipherKey)
        return keyCipher.doFinal(data)
    }

    fun recoverKeys(){
        val keyFac = KeyFactory.getInstance("RSA")
        val user = File(pkDirectory).readBytes()
        val privKeyU = PKCS8EncodedKeySpec(user)
        userPrivateKey = keyFac.generatePrivate(privKeyU)
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
}