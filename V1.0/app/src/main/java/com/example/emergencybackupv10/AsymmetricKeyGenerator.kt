package com.example.emergencybackupv10

import android.content.Context
import android.os.Environment
import android.security.keystore.KeyProtection
import java.io.File
import java.security.*

class AsymmetricKeyGenerator(val context : Context) {
    private var public : PublicKey? = null
    private var private : PrivateKey? = null
    private lateinit var publicDirectory : String
    private lateinit var privateDirectory : String

    fun generateKeys() {
        val keysGenerator = KeyPairGenerator.getInstance("RSA")
        keysGenerator.initialize(1024)
        val keys : KeyPair = keysGenerator.generateKeyPair()
        public = keys.public
        private = keys.private
    }

    fun savePublicKey(){
        val pubFile = File(context.filesDir, "userPubKey.pk")
        publicDirectory = pubFile.absolutePath
        pubFile.writeBytes(public!!.encoded)
    }

    fun savePrivateKey(){
        var parentFile : File?
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
            parentFile = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        else
            parentFile = context.filesDir
        val privFile = File(parentFile, "userPrivKey.pk")
        privateDirectory = privFile.absolutePath
        privFile.writeBytes(private!!.encoded)
    }

    fun getKeysDirectories() : ArrayList<String>{
        var directories = ArrayList<String>()
        directories.add(publicDirectory)
        directories.add(privateDirectory)
        return directories
    }
}