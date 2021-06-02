package com.example.emergencybackupv10.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import com.example.emergencybackupv10.KeyManager
import com.example.emergencybackupv10.R
import com.example.emergencybackupv10.networking.interfaces.ServerResponse
import com.example.emergencybackupv10.networking.interfaces.UpdateUser
import com.example.emergencybackupv10.utils.AlertUtils
import kotlinx.android.synthetic.main.fragment_change_key.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChangeKey : Fragment() {
    private var alertUtils: AlertUtils = AlertUtils()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var retrofit: Retrofit;
    private lateinit var url: String;
    private lateinit var updateService: UpdateUser;
    private lateinit var publicKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        return inflater.inflate(R.layout.fragment_change_key, container, false)
    }

    override fun onStart() {
        super.onStart()
        url = getString(R.string.host_url)
        retrofit =
            Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create())
                .build()
        updateService = retrofit.create(UpdateUser::class.java)
        btn_change_key.setOnClickListener { v ->
            val keyMan = KeyManager(requireActivity().applicationContext)
            keyMan.generateKeys()
            publicKey = keyMan.getPubKeyAsString()
            updateKey()
        }
    }

    fun updateKey(){
        val t = sharedPreferences.getString(getString(R.string.CONFIG_TOKEN), "")!!
        val p = update_key_password.text.toString();
        val call: Call<ServerResponse> = updateService.update_key(
            auth = t,
            password = p,
            new_key = publicKey
        )
        call.enqueue(object : Callback<ServerResponse> {
            override fun onFailure(call: Call<ServerResponse>?, t: Throwable?) {
                alertUtils.topToast(requireContext(), "Hubo un error por favor intentalo otra vez mas tarde")
            }
            override fun onResponse(
                call: Call<ServerResponse>?,
                response: retrofit2.Response<ServerResponse>?
            ) {
                if(response?.body()?.error==true){
                    alertUtils.topToast(requireContext(), "Se ha cambiado la contraseña")
                }else{
                    alertUtils.topToast(requireContext(), response?.body()?.message.toString())
                }
            }
        }
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChangeKey()
    }
}