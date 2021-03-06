package com.example.emergencybackupv10

import android.content.Context
import com.example.emergencybackupv10.networking.HttpQ
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.auth0.android.jwt.JWT
import com.example.emergencybackupv10.networking.CertificateAddition
import com.example.emergencybackupv10.networking.HttpsQ
import com.example.emergencybackupv10.utils.AlertUtils
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.StringBuilder
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import java.nio.CharBuffer
import javax.net.ssl.HttpsURLConnection


class Login : AppCompatActivity() {
    private lateinit var queue: RequestQueue;
    private lateinit var url: String;
    private val alertUtils: AlertUtils = AlertUtils();
    private lateinit var sharedPreferences: SharedPreferences;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        queue = HttpQ.getInstance(this.applicationContext).requestQueue
        url = getString(R.string.host_url) + getString(R.string.api_login)
        val emergency = intent.getBooleanExtra(getString(R.string.EMERGENCY), false)
        checkForToken(emergency)
    }

    public fun checkForToken(emergency: Boolean) {
        val t = sharedPreferences.getString(getString(R.string.CONFIG_TOKEN), null)
        if (t != null) {
            val intent = Intent(this, Home::class.java)
            intent.putExtra(
                getString(R.string.CONFIG_WAS_LOGED_IN), true
            )
            intent.putExtra(getString(R.string.EMERGENCY), emergency)
            startActivity(intent)
        }
    }

    public fun signUp(view: View) {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }

    public fun logIn(view: View) {
        if (!isValidEmail(login_email.text.toString())) {
            alertUtils.topToast(this, "Dirección de correo invalida")
            return
        }

        val postRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->

                var token = response.toString()
                val jwt = JWT(token)
                //Save the token to local storage
                with(sharedPreferences.edit()) {
                    putString(getString(R.string.CONFIG_TOKEN), token)
                    putString(
                        getString(R.string.CONFIG_SERVER_PEM_CERTIFICATE),
                        jwt.getClaim(getString(R.string.CONFIG_SERVER_PEM_CERTIFICATE)).asString()
                    )
                    apply()
                }
                val pubKey = sharedPreferences.getString(getString(R.string.CONFIG_SERVER_PEM_CERTIFICATE), null)
                println(pubKey)

                //Parse the token as parameter to teh next activity

                val intent = Intent(this, Home::class.java)
                intent.putExtra(
                    getString(R.string.CONFIG_WAS_LOGED_IN), false
                )
                intent.putExtra(
                    getString(R.string.ARG_BU_AVAILABLE),
                    jwt.getClaim(getString(R.string.ARG_BU_AVAILABLE)).asBoolean()
                )

                intent.putExtra(
                    getString(R.string.ARG_NAME),
                    jwt.getClaim(getString(R.string.ARG_NAME)).asString()
                )

                intent.putExtra(
                    getString(R.string.ARG_ID),
                    jwt.getClaim(getString(R.string.ARG_ID)).asString()
                )
                startActivity(intent);
            },
            Response.ErrorListener { error ->
                val msg: String = HttpQ.getInstance(this).getErrorMsg(error)
                alertUtils.topToast(this, msg)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = login_email.text.toString()
                params["password"] = login_password.text.toString()
                return params
            }

        }
        HttpQ.getInstance(this).addToRequestQueue(postRequest)
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
        }
    }


}
