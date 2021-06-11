package com.example.emergencybackupv10

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.preference.PreferenceManager
import java.io.File
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

class KeyManager(val context : Context) {
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

        Log.i("New public key", "\n${Base64.encodeToString(keys.public.encoded, Base64.DEFAULT)}")
        //Save private key//////////////////////////////////////////////////////////////////////////
        var parentFile : File?
        println(Environment.getExternalStorageState())
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
            parentFile = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        else
            parentFile = context.filesDir
        val privFile = File(parentFile, "userPrivKey.pk")
        privateDirectory = privFile.path
        privFile.writeBytes(keys.private.encoded)

        Log.i("New private key", "\n${Base64.encodeToString(keys.private.encoded, Base64.DEFAULT)}")
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

    /*@Returns array with two public keys, the first one is the users public key, the second is the
    servers*/
    fun recoverPublicKeys(): ArrayList<PublicKey>{
        /*User public key comes from a file */
        var keys = ArrayList<PublicKey>()
        val keyFac = KeyFactory.getInstance("RSA")
        val user = File(context.filesDir, "userPubKey.pk").readBytes()
        val pubKeyU = X509EncodedKeySpec(user)
        keys.add(keyFac.generatePublic(pubKeyU))
        /*Servers public key is saved in shared preferences after login*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val pem_certificate:String = sharedPreferences.getString(context.getString(R.string.CONFIG_SERVER_PEM_CERTIFICATE),"")!!
        val server_key_string = pem_certificate
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace(System.lineSeparator(), "")
            .replace("-----END PUBLIC KEY-----", "");
        val encoded_key :ByteArray= Base64.decode(server_key_string,0)
        val pubKeyS = X509EncodedKeySpec(encoded_key)
        keys.add(keyFac.generatePublic(pubKeyS))
        return keys
    }

    fun recoverPrivateKey(location: Uri?): PrivateKey{
        val keyFac = KeyFactory.getInstance("RSA")
        var user : ByteArray? = null
        location?.let {
            context.contentResolver.openInputStream(it)?.use { reader ->
                user = reader.readBytes()
            }
        }
        val privKeyU = PKCS8EncodedKeySpec(user)
        return keyFac.generatePrivate(privKeyU)
    }
}