package com.example.emergencybackupv10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //Agregar valores al spinner desde el res/values/questions.xml
        val spinner:Spinner = findViewById<Spinner>(R.id.spinner);
        ArrayAdapter.createFromResource(this,
            R.array.questions_arr,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
        }
    }

    fun signUp(view:View){
        Log.i("Sing up process","Se inicio el proceso de registro(?)")

    }
}
