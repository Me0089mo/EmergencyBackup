package com.example.emergencybackupv10

import com.example.emergencybackupv10.networking.HttpQ
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.auth0.android.jwt.JWT
import com.example.emergencybackupv10.utils.AlertUtils
import kotlinx.android.synthetic.main.activity_signup.*


class SignUp : AppCompatActivity() {
    private lateinit var queue: RequestQueue;
    private lateinit var url: String;
    private lateinit var pubKey: String;
    private lateinit var userKeysFiles : ArrayList<String>
    private val alertUtils: AlertUtils = AlertUtils();
    private lateinit var keyMan:KeyManager;
    private lateinit var sharedPreferences:SharedPreferences;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        queue = HttpQ.getInstance(this.applicationContext).requestQueue
        url = getString(R.string.host_url) + getString(R.string.api_register)
        keyMan = KeyManager(this.applicationContext)
    }

    fun signUp(view: View) {

        if (!confirmEmail()) {
            alertUtils.topToast(this, "Las direcciones de correo no coinciden")
            return;
        }
        if (!confirmPassword()) {
            alertUtils.topToast(this, "Las contraseñas no coinciden")
            return;
        }
        //Generate keys
        keyMan.generateKeys()
        pubKey = keyMan.getPubKeyAsString()
        userKeysFiles = keyMan.getKeysDirectories()

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
                val intent = Intent(this, RegistrationSuccess::class.java)
                startActivity(intent);
            },
            Response.ErrorListener { error ->
                val msg: String  = HttpQ.getInstance(this).getErrorMsg(error)
                alertUtils.topToast(this, msg)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = signup_email_input.text.toString()
                params["password"] = signup_password_input.text.toString()
                params["name"] = signup_name_input.text.toString()
                params["pub_key"] = pubKey
                return params
            }

        }
        HttpQ.getInstance(this).addToRequestQueue(postRequest)
    }

    fun confirmEmail(): Boolean {
        if (!isValidEmail(signup_email_input.text.toString())) {
            return false
        }
        return signup_email_input.text.toString() == signup_email_confirm.text.toString()
    }

    fun confirmPassword(): Boolean {
        return signup_password_confirm.text.toString() == signup_password_input.text.toString()
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}
