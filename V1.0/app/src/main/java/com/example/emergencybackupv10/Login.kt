package com.example.emergencybackupv10

import HttpQ
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
//import androidx.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.auth0.android.jwt.JWT
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.reflect.typeOf


class Login : AppCompatActivity() {
    private lateinit var queue: RequestQueue;
    private lateinit var url: String;
    private val alertUtils: AlertUtils = AlertUtils();
    private lateinit var sharedPreferences:SharedPreferences;
    private lateinit var userKeysFiles : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val assKeyGen = AsymmetricKeyGenerator(this.applicationContext)
        assKeyGen.generateKeys()
        assKeyGen.savePublicKey()
        assKeyGen.savePrivateKey()
        userKeysFiles = assKeyGen.getKeysDirectories()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        /*queue = HttpQ.getInstance(this.applicationContext).requestQueue
        url = getString(R.string.host_url) + getString(R.string.api_login)
        checkForToken();*/
    }

    public fun checkForToken(){
        val t = sharedPreferences.getString(getString(R.string.CONFIG_TOKEN),null)
        if (t != null){
            val intent = Intent(this, Home::class.java)
            intent.putExtra(
                    getString(R.string.CONFIG_WAS_LOGED_IN),true
            )
            startActivity(intent)
        }
    }

    public fun signUp(view: View) {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }

    public fun logIn(view: View) {

        /*if (!isValidEmail(login_email.text.toString())) {
            alertUtils.topToast(this, "Dirección de correo invalida")
            return
        }
        val postRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                var token = response.toString()
                //Save the token to local storage
                with (sharedPreferences.edit()) {
                    putString(getString(R.string.CONFIG_TOKEN), token)
                    apply()
                }

                //Parse the token as parameter to teh next activity
                val jwt = JWT(token)*/
                val intent = Intent(this, Home::class.java)
                intent.putExtra(getString(R.string.ARG_PUB_KEY), userKeysFiles[0])
                intent.putExtra(getString(R.string.ARG_PRIV_KEY), userKeysFiles[1])
                intent.putExtra(
                    getString(R.string.CONFIG_WAS_LOGED_IN),false
                )

                intent.putExtra(
                    getString(R.string.ARG_BU_AVAILABLE),
                    jwt.getClaim(getString(R.string.ARG_BU_AVAILABLE)).asBoolean()
                )

                intent.putExtra(
                    getString(R.string.ARG_NAME),
                    jwt.getClaim(getString(R.string.ARG_NAME)).asString()
                );

                intent.putExtra(
                    getString(R.string.ARG_ID),
                    jwt.getClaim(getString(R.string.ARG_ID)).asString()
                );*/
                startActivity(intent);
            /*},
            Response.ErrorListener { error ->
                val msg: String  = HttpQ.getInstance(this).getErrorMsg(error)
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
        HttpQ.getInstance(this).addToRequestQueue(postRequest)*/
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }


}
