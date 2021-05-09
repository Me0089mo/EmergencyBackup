package com.example.emergencybackupv10.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.emergencybackupv10.R
import com.example.emergencybackupv10.utils.AlertUtils
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_change_email.*

class ChangeEmail : Fragment() {
    private var newEmail: String? = null
    private var alertUtils: AlertUtils = AlertUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_email, container, false)
    }

    override fun onStart(){
        super.onStart()
        btn_change_email.setOnClickListener { v ->
            if(confirmEmail()){
                newEmail = txt_new_email.text.toString()
                //Subir nuevo e-mail
            }
            else
                alertUtils.topToast(this.requireContext(), "Las direcciones de correo no coinciden")
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            ChangeEmail()
    }

    fun confirmEmail(): Boolean {
        if (!isValidEmail(txt_new_email.text.toString())) {
            return false
        }
        return txt_new_email.text.toString() == txt_confirm_email.text.toString()
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}