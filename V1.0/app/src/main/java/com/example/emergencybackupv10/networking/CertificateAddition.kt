package com.example.emergencybackupv10.networking

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.Base64
import android.util.Log
import java.io.*
import java.net.URL
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

class CertificateAddition(val appContext: Context) {
    // Load CAs from an InputStream
    private lateinit var trustManager: TrustManagerFactory
    private lateinit var keyStore: KeyStore

    init {
        chargeCA()
        generateTrustManager()
    }

    private fun chargeCA(){
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
        val inputRes = appContext.resources.openRawResource(appContext.resources.getIdentifier("certificate", "raw", appContext.packageName))
        val caInput: InputStream = BufferedInputStream(inputRes)

        val ca: X509Certificate = caInput.use {
            cf.generateCertificate(it) as X509Certificate
        }
        //Log.i("CA", Base64.encodeToString(ca.encoded, Base64.DEFAULT))

        // Create a KeyStore containing our trusted CAs
        val keyStoreType = KeyStore.getDefaultType()
        keyStore = KeyStore.getInstance(keyStoreType).apply {
            load(null, null)
            setCertificateEntry("ServerCA", ca)
        }
    }

    private fun generateTrustManager(){
        // Create a TrustManager that trusts the CAs inputStream our KeyStore
        val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
        trustManager = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
            init(keyStore)
        }
    }

    fun getSslSocketConnection(/*serverUrl: String*/): SSLSocketFactory{
        // Create an SSLContext that uses our TrustManager
        val sslContext: SSLContext = SSLContext.getInstance("TLS").apply {
            init(null, trustManager.trustManagers, null)
        }
        return sslContext.socketFactory
        // Tell the URLConnection to use a SocketFactory from our SSLContext
        //val url = URL(serverUrl)
        //val urlConnection = url.openConnection() as HttpsURLConnection
        //urlConnection.sslSocketFactory = sslContext.socketFactory
        //return urlConnection
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