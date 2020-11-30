package com.example.emergencybackupv10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_signup.*


class SignUp : AppCompatActivity() {
    private lateinit var queue: RequestQueue;
    private lateinit var url: String;
    private val alertUtils: AlertUtils = AlertUtils();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        queue = HttpQ.getInstance(this.applicationContext).requestQueue
        url = getString(R.string.host_url) + getString(R.string.api_register)
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

        val postRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
//                TODO change to success screen
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            },
            Response.ErrorListener { error ->
                alertUtils.topToast(this, "Hubo un error en el proceso de registro")
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = signup_email_input.text.toString()
                params["password"] = signup_password_input.text.toString()
                params["name"] = signup_name_input.text.toString()
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
