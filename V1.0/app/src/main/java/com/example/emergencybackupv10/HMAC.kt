package com.example.emergencybackupv10

import javax.crypto.Cipher
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
        keyGenerator.init(128)
        key = keyGenerator.generateKey()
    }

    fun calculateMac(data: ByteArray){
        macInstance.update(data)
    }

    fun finalizeMac(){
        tag = macInstance.doFinal()

    }

    fun verifyMac(tagCalculated : ByteArray) : Boolean = tag.contentEquals(tagCalculated)
}