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
        fun readFile(path : Uri, cipheredDataPath : String, fileName : String)
        fun cipherData(data : ByteArray)
        fun initializeCipher()
        fun finalizeCipher()
}