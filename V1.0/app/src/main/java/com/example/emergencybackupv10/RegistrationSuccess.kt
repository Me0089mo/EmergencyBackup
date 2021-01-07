package com.example.emergencybackupv10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class RegistrationSuccess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_success)
    }

    fun goHome(v: View){
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}