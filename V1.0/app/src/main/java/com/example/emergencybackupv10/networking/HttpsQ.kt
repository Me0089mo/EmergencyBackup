package com.example.emergencybackupv10.networking

import android.content.Context
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.Volley
import javax.net.ssl.SSLSocketFactory

class HttpsQ(context: Context, sslSock: SSLSocketFactory) {
    companion object {
        @Volatile
        private var INSTANCE: HttpsQ? = null
        fun getInstance(context: Context, sslSock: SSLSocketFactory) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HttpsQ(context, sslSock).also {
                    INSTANCE = it
                }
            }
    }

    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        //Volley.newRequestQueue(context.applicationContext, HurlStack(HurlStack.UrlRewriter(url), sslSock))
        Volley.newRequestQueue(context.applicationContext, HurlStack(null, sslSock))
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    fun getErrorMsg (error: VolleyError) : String {
        /*Log.i("Error", error.message)
        Log.i("Cause", error.cause.toString())*/
        if (error is NoConnectionError){
            return "No hay conexión"
        }
        if (error is TimeoutError){
            return "No hay respuesta de parte del servidor"
        }
        if(error is AuthFailureError){
            return "Credenciales incorrectas"
        }
        return "Error desconocido, por favor comprueba tus credenciales"
    }

}