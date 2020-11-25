package com.example.emergencybackupv10

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import HttpQ
import com.android.volley.RequestQueue
import kotlinx.android.synthetic.main.activity_login.*


class Login : AppCompatActivity() {
    private lateinit var queue : RequestQueue ;
    private lateinit var url :String ;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        queue = HttpQ.getInstance(this.applicationContext).requestQueue
        url = getString(R.string.host_url)+getString(R.string.api_login)
    }

    public fun signUp(view: View){
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }

    public fun logIn(view: View){
        var resp:String="";
        val postRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response -> resp = response.toString() },
            Response.ErrorListener { error -> Log.d("ERROR", error.message.toString()) }
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = login_email.text.toString()
                params["password"] = login_password.text.toString()
                return params
            }

        }
        HttpQ.getInstance(this).addToRequestQueue(postRequest)
        Log.d("RESPONSE", resp)
//        if(correctCreds){
//            val intent = Intent(this, Home::class.java)
//            startActivity(intent)
//        }else{
//            val toast = Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT)
//            toast.setGravity(Gravity.TOP,0,0)
//            toast.show()
//        }
    }


}
