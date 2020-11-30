package com.example.emergencybackupv10

import HttpQ
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.auth0.android.jwt.JWT
import kotlinx.android.synthetic.main.activity_login.*


class Login : AppCompatActivity() {
    private lateinit var queue: RequestQueue;
    private lateinit var url: String;
    private val alertUtils: AlertUtils = AlertUtils();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        queue = HttpQ.getInstance(this.applicationContext).requestQueue
        url = getString(R.string.host_url) + getString(R.string.api_login)
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
        var token: String = "";
        val postRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                token = response.toString()
                val jwt = JWT(token)
                val intent = Intent(this, Home::class.java)
                intent.putExtra(
                    R.string.ARG_BU_AVAILABLE.toString(),
                    jwt.getClaim(R.string.ARG_BU_AVAILABLE.toString()).asBoolean()
                )
                intent.putExtra(
                    R.string.ARG_NAME.toString(),
                    jwt.getClaim(R.string.ARG_NAME.toString()).asString()
                )
                intent.putExtra(
                    R.string.ARG_ID.toString(),
                    jwt.getClaim(R.string.ARG_ID.toString()).asString()
                )
                startActivity(intent)
            },
            Response.ErrorListener { error ->
                alertUtils.topToast(this, "Credenciales incorrectas")
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
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }


}
