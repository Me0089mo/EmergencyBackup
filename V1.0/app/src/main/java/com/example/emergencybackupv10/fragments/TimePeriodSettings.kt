package com.example.emergencybackupv10.fragments

import android.content.Context
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
import kotlinx.android.synthetic.main.fragment_time_period_config.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TimePeriodSettings : Fragment() {
    private var alertUtils: AlertUtils = AlertUtils()
    private lateinit var sharedPreferences: SharedPreferences
//    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)


        return inflater.inflate(R.layout.fragment_time_period_config, container, false)
    }

    override fun onStart() {
        super.onStart()
        fragment_time_period_picker.maxValue = 7
        fragment_time_period_picker.minValue = 1
        fragment_time_period_picker.value = sharedPreferences.getInt(getString(R.string.CONFIG_TIME_PERIOD),1)
        time_period_btn_save_changes.setOnClickListener {view -> updateTimePeriod() }
    }



    fun updateTimePeriod(){
        val period = fragment_time_period_picker.value
        with(sharedPreferences.edit()) {
            putInt(getString(R.string.CONFIG_TIME_PERIOD),period )
            apply()
        }
        alertUtils.topToast(requireContext(),"Se ha actualizado el periodo de tiempo")
        parentFragmentManager.popBackStack()


    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TimePeriodSettings()
    }
}