package com.example.pruebascifrado

import android.content.Context
import android.net.Uri

interface Cifrador {
        val path: Uri
        val contentContext: Context
        var data:ByteArray?
        fun readFile()
        fun cipherData(): ByteArray
}