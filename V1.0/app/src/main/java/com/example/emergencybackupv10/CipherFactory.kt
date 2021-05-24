package com.example.emergencybackupv10

import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream

abstract class CipherFactory {
    abstract val cipher : Cipher
    abstract val keyCipher : Cipher
    abstract val keyManager : KeyManager
    abstract val compressor : Compressor
    abstract var cipheredOutput : CipherOutputStream
    abstract var fileOutStream : FileOutputStream
    abstract var byteOutStream : ByteArrayOutputStream
    abstract var mac : HMAC
    abstract fun processFile(path : Uri, fileName : String)
    abstract fun processData(data : ByteArray, numBytes: Int)
    abstract fun createOutputFile(path : String, fileName : String)
    abstract fun processKey(cipherKey : Key, data : ByteArray) : ByteArray
}