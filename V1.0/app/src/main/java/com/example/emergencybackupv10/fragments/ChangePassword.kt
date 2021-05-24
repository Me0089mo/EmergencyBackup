package com.example.emergencybackupv10.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import com.example.emergencybackupv10.R
import com.example.emergencybackupv10.networking.interfaces.ServerResponse
import com.example.emergencybackupv10.networking.interfaces.UpdateUser
import com.example.emergencybackupv10.utils.AlertUtils
import kotlinx.android.synthetic.main.fragment_change_email.btn_change_email
import kotlinx.android.synthetic.main.fragment_change_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChangePassword : Fragment() {
    private var newPassword: String? = null
    private var alertUtils: AlertUtils = AlertUtils()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var retrofit: Retrofit;
    private lateinit var url: String;
    private lateinit var updateService: UpdateUser;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onStart() {
        super.onStart()
        url = getString(R.string.host_url)
        retrofit =
            Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create())
                .build()
        updateService = retrofit.create(UpdateUser::class.java)
        btn_change_email.setOnClickListener { v: View? ->
            updatePassword()
        }
    }

    public fun updatePassword() {
        val t = sharedPreferences.getString(getString(R.string.CONFIG_TOKEN), "")
        if (confirm_password()) {
            //update email
            val call: Call<ServerResponse> = updateService.update_password(
                auth = t.toString(),
                old_password = txt_old_pass.text.toString(),
                new_password = txt_confirm_pass.text.toString()

            )
            call.enqueue(object : Callback<ServerResponse> {
                override fun onFailure(call: Call<ServerResponse>?, t: Throwable?) {
                    alertUtils.topToast(requireContext(), "Hubo un error por favor intentalo otra vez mas tarde")
                }
                override fun onResponse(
                    call: Call<ServerResponse>?,
                    response: Response<ServerResponse>?
                ) {
                    if(response?.body()?.error==true){
                        alertUtils.topToast(requireContext(), "Se ha cambiado la contraseña")
                    }else{
                        alertUtils.topToast(requireContext(), response?.body()?.message.toString())
                    }


                }
            }
            );
        } else {
            alertUtils.topToast(this.requireContext(), "Las contraseñas no coinciden")
        }
    }

    private fun confirm_password(): Boolean {
        return txt_new_pass.text.toString() == txt_confirm_pass.text.toString()
    }
}