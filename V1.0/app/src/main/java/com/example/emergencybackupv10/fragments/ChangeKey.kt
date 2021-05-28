package com.example.emergencybackupv10.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.auth0.android.jwt.JWT
import com.example.emergencybackupv10.Home
import com.example.emergencybackupv10.KeyManager
import com.example.emergencybackupv10.R
import com.example.emergencybackupv10.networking.HttpQ
import com.example.emergencybackupv10.networking.interfaces.ServerResponse
import com.example.emergencybackupv10.networking.interfaces.UpdateUser
import com.example.emergencybackupv10.utils.AlertUtils
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_change_key.*
import kotlinx.android.synthetic.main.fragment_change_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChangeKey.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChangeKey : Fragment() {
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
        // Inflate the layout for this fragment
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
            updateKey()
        }
    }

    fun verifyIdentity(){

    }

    fun updateKey(){
        val t = sharedPreferences.getString(getString(R.string.CONFIG_TOKEN), "")
        val call: Call<ServerResponse> = updateService.update_key(
            auth = t.toString(),
            new_key = txt_old_pass.text.toString()
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