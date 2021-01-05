package com.example.emergencybackupv10

import android.content.Context
import android.os.Environment
import android.util.Base64
import java.io.File
import java.security.*

class KeyManager(val context : Context) {
    private var public : PublicKey? = null
    private var private : PrivateKey? = null
    private lateinit var publicDirectory : String
    private lateinit var privateDirectory : String

    fun generateKeys(){
        val keysGenerator = KeyPairGenerator.getInstance("RSA")
        keysGenerator.initialize(1024)
        val keys : KeyPair = keysGenerator.generateKeyPair()
        //Save public key///////////////////////////////////////////////////////////////////////////
        val pubFile = File(context.filesDir, "userPubKey.pk")
        publicDirectory = pubFile.absolutePath
        pubFile.writeBytes(keys.public.encoded)
        //Save private key//////////////////////////////////////////////////////////////////////////
        var parentFile : File?
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
            parentFile = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        else
            parentFile = context.filesDir
        val privFile = File(parentFile, "userPrivKey.pk")
        privateDirectory = privFile.absolutePath
        privFile.writeBytes(keys.private.encoded)
    }

    fun getPubKeyAsString():String{
        val bytes = File(context.filesDir, "userPubKey.pk").readBytes()
        val encodedBytes = Base64.encode(bytes,0)
        return String(encodedBytes)
    }

    fun getKeysDirectories() : ArrayList<String>{
        var directories = ArrayList<String>()
        directories.add(publicDirectory)
        directories.add(privateDirectory)
        return directories
    }
}