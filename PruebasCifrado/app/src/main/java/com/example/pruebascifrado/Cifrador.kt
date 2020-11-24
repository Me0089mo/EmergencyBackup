package com.example.pruebascifrado

import android.content.Context
import android.net.Uri
import javax.crypto.Cipher
import javax.crypto.KeyGenerator

interface Cifrador {
        val contentContext : Context
        val cipher : Cipher
        val keyGenerator : KeyGenerator
        var data : ByteArray?
        var cipherText : ByteArray?
        fun readFile(path : Uri, fileSize : Long)
        fun cipherData(data : ByteArray, cipherOffset: Int, inputLen : Int)
        fun initializeCipher(fileSize : Long)
        fun finalizeCipher(cipherOffset: Int)
}