package com.example.emergencybackupv10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_registration_success.*

class RegistrationSuccess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_success)
        val key_loc = intent.getStringExtra(getString(R.string.KEY_LOCATION))
        txt_key_location.text = key_loc
        txt_key_location.setOnClickListener{ v ->

        }
    }

    fun goHome(v: View){
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}