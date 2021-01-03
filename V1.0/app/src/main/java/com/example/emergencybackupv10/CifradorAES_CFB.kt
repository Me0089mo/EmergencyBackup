package com.example.emergencybackupv10

import android.content.Context
import android.net.Uri
import java.io.*
import java.security.*
import java.security.spec.X509EncodedKeySpec
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.crypto.*

class CifradorAES_CFB(val contentContext: Context) {

    private val cipher:Cipher = Cipher.getInstance("AES/CFB/PKCS5PADDING")
    private val keyGenerator = KeyGenerator.getInstance("AES")
    private val keyCipher = Cipher.getInstance("RSA/ECB/OAEPPADDING")
    private lateinit var cipheredFile : File
    private lateinit var cipheredOutput : CipherOutputStream
    private lateinit var fileOutStream : FileOutputStream
    private lateinit var byteOutStream : ByteArrayOutputStream
    private lateinit var mac : HMAC
    private lateinit var userPublicKey : PublicKey
    private lateinit var serverPublicKey : PublicKey
    private lateinit var key : SecretKey

    fun cipherFile(path : Uri, cipheredDataPath : String, fileName : String){
        createDestinationFile(cipheredDataPath, fileName)
        contentContext.contentResolver.openInputStream(path)?.use { reader ->
            val inputStream = BufferedInputStream(reader)
            val byteOutReadStream = ByteArrayOutputStream()
            val zipOutStream = ZipOutputStream(byteOutReadStream)
            val zipEntry = ZipEntry(fileName)//path.path)
            zipOutStream.putNextEntry(zipEntry)
            val readingArray = ByteArray(10240)
            var readLong:Int
            var byteOutStreamConv:ByteArray
            var cipherOffset = 0
            initializeCipher()
            //Writing the new entry
            fileOutStream.write(byteOutReadStream.toByteArray())
            byteOutReadStream.reset()
            do{
                readLong = inputStream.read(readingArray)
                if(readLong == -1) break
                zipOutStream.write(readingArray, 0, readLong)
                byteOutStreamConv = byteOutReadStream.toByteArray()
                if(byteOutStreamConv.isNotEmpty()) {
                    cipherData(byteOutStreamConv)
                    cipherOffset += byteOutStreamConv.size
                }
                byteOutReadStream.reset()
            }while(readLong >= 0)
            zipOutStream.closeEntry()
            zipOutStream.close()
            finalizeCipher()
        }
    }

    private fun initializeCipher(){
        keyGenerator.init(128)
        key = keyGenerator.generateKey()
        cipher.init(Cipher.ENCRYPT_MODE, key)
        mac = HMAC()
        mac.initializeMac()
        recoverKeys()
        fileOutStream.write(cipherKey(userPublicKey, key.encoded))
        fileOutStream.write(cipher.iv)
    }

    private fun cipherData(data : ByteArray){
        cipheredOutput.write(data)
        val aux = byteOutStream.toByteArray()
        fileOutStream.write(aux)
        mac.calculateMac(aux)
        byteOutStream.reset()
    }

    private fun finalizeCipher(){
        cipheredOutput.close()
        /*fileOutStream.write(byteOutStream.toByteArray())
        mac.calculateMac(byteOutStream.toByteArray())*/
        mac.finalizeMac()
        fileOutStream.channel.position(0)
        fileOutStream.write(mac.tag)
        print("Original tag: ")
        mac.tag.forEach { b -> print("$b, ") }
        print("\nOriginal mac key: ")
        mac.key.encoded.forEach { b -> print("$b, ") }
        print("\nOriginal cipher key: ")
        key.encoded.forEach { b -> print("$b, ") }
        print("\nOriginal iv: ")
        cipher.iv.forEach { b -> print("$b, ") }
        print("\n")

        fileOutStream.write(cipherKey(serverPublicKey, mac.key.encoded))
        fileOutStream.close()
    }

    private fun createDestinationFile(path : String, fileName : String){
        cipheredFile = File("$path/$fileName")
        fileOutStream = FileOutputStream(cipheredFile)
        fileOutStream.channel.position(160)
        byteOutStream = ByteArrayOutputStream()
        cipheredOutput = CipherOutputStream(byteOutStream, cipher)
    }

    private fun cipherKey(cipherKey : PublicKey, data : ByteArray) : ByteArray{
        keyCipher.init(Cipher.ENCRYPT_MODE, cipherKey)
        return keyCipher.doFinal(data)
    }

    private fun recoverKeys(){
        val keyFac = KeyFactory.getInstance("RSA")
        val user = File(contentContext.filesDir, "userPubKey.pk").readBytes()
        val pubKeyU = X509EncodedKeySpec(user)
        userPublicKey = keyFac.generatePublic(pubKeyU)
        val server = File(contentContext.filesDir, "userPubKey.pk").readBytes()
        val pubKeyS = X509EncodedKeySpec(server)
        serverPublicKey = keyFac.generatePublic(pubKeyS)
    }

    private fun generateNewName(name:String):String{
        var newName = ""
        for (index in (name.length-1).downTo(0)){
            if (name[index] == '.'){
                newName = name.substring(0, index) + "Ciphered"
                newName += name.substring(index, name.length)
                break
            }
        }
        return newName
    }
}