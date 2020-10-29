package com.example.emergencybackupv10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnSiguiente = findViewById<Button>(R.id.btnRegistrarse)

        btnSiguiente.setOnClickListener(View.OnClickListener { v ->
            val intent = Intent(this, SignIn2::class.java)
            startActivity(intent)
        })
    }
}
