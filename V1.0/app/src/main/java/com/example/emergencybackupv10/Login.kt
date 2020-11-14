package com.example.emergencybackupv10

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    public fun signUp(view:View){
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }

    public fun logIn(view:View){
        val correctCreds = true
        Log.i("Login", "Login started")
        if(correctCreds){
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }else{
            val toast = Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP,0,0)
            toast.show()
        }
    }


}
