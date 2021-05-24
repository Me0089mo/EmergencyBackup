package com.example.emergencybackupv10.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.example.emergencybackupv10.R
import com.example.emergencybackupv10.networking.interfaces.ServerResponse
import com.example.emergencybackupv10.networking.interfaces.UpdateUser
import com.example.emergencybackupv10.utils.AlertUtils
import kotlinx.android.synthetic.main.fragment_change_email.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChangeEmail : Fragment() {
    private var newEmail: String? = null
    private var alertUtils: AlertUtils = AlertUtils()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var retrofit:Retrofit;
    private lateinit var  url:String;
    private lateinit var updateService: UpdateUser;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        return inflater.inflate(R.layout.fragment_change_email, container, false)
    }

    override fun onStart(){
        super.onStart()
        url = getString(R.string.host_url)
        retrofit = Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build()
        updateService= retrofit.create(UpdateUser::class.java)
        btn_change_email .setOnClickListener { v: View? ->
            updateEmail()
        }
    }

    public fun updateEmail(){
        val t = sharedPreferences.getString(getString(R.string.CONFIG_TOKEN), "")
        if(confirmEmail()){
            newEmail = txt_new_email.text.toString()
            //update email
            val call:Call<ServerResponse> = updateService.update_email(auth =t.toString() ,new_email = newEmail.toString())
            call.enqueue(object : Callback<ServerResponse> {
                override fun onFailure(call: Call<ServerResponse>?, t: Throwable?) {
                    Toast.makeText(context, "Hubo un error, por favor vuelve a intentarlo", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<ServerResponse>?, response: Response<ServerResponse>?) {
                    Log.i("retrofit response", response.toString())
                }
            }
            );
        }else{
            alertUtils.topToast(this.requireContext(), "Las direcciones de correo no coinciden")
        }
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