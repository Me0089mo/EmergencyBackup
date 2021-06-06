package com.example.emergencybackupv10

import android.util.Log
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey

class HMAC constructor(){
    private val keyGenerator = KeyGenerator.getInstance("HmacSHA256")
    lateinit var macInstance : Mac
    lateinit var tag : ByteArray
    lateinit var key : SecretKey

    constructor(key : SecretKey) : this() {
        this.key = key
    }

    fun initializeMac(){
        if(!this::key.isInitialized) generateKey()
        macInstance = Mac.getInstance("HmacSHA256")
        macInstance.init(key)
    }

    private fun generateKey(){
        keyGenerator.init(256)
        key = keyGenerator.generateKey()
    }

    fun calculateMac(data: ByteArray, numBytes: Int){
        macInstance.update(data, 0, numBytes)
    }

    fun finalizeMac(){
        tag = macInstance.doFinal()
    }

    fun verifyMac(tagRecovered : ByteArray) : Boolean{
        Log.i("Tag recibido", "\n${print_bytes(tagRecovered)}")
        Log.i("Tag calculado", "\n${print_bytes(tag)}")
        return tag.contentEquals(tagRecovered)
    }

    fun print_bytes(bytes: ByteArray): String? {
        val sb = StringBuilder()
        sb.append("[ ")
        var i = 0
        for (b in bytes) {
            i++
            sb.append(String.format("0x%02X ", b))
            if(i%8 == 0)
                sb.append("\n")
        }
        sb.append("]")
        return sb.toString()
    }

}