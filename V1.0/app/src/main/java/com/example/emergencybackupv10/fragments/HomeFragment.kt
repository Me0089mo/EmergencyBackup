package com.example.emergencybackupv10.fragments

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import androidx.preference.PreferenceManager
import com.example.emergencybackupv10.Backup
import com.example.emergencybackupv10.DescifradorAES_CFB
import com.example.emergencybackupv10.Home
import com.example.emergencybackupv10.R
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.File

private const val ARG_BU_AVAILABLE = "backUpAvailable"

class HomeFragment : Fragment() {

    private var backUpOnCloud: Boolean = false
    private var cipheredDataPath: String? = null
    private var decipheredDataPath : String? = null
    private var publicKeyFile : String? = ""
    private var privateKeyFile : String? = ""
    private var sharedPreferences : SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            backUpOnCloud = it.getBoolean(ARG_BU_AVAILABLE)
            publicKeyFile = it.getString(getString(R.string.ARG_PUB_KEY))
            privateKeyFile = it.getString(getString(R.string.ARG_PRIV_KEY))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStart() {
        super.onStart()
        setText()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)

        btn_cifrar.setOnClickListener { v: View? ->
            if (v != null) {
                (activity as Home).startBackup()
            }
        }

        btn_decifrar.setOnClickListener { v : View? ->
            v?.let { (activity as Home).restoreBackup() }
        }
    }


    private fun setText() {
        if(backUpOnCloud){
            text_home.text = "Hay un respaldo en la nube"
        }else{
            text_home.text = "No hay un respaldo pendiente\n Hurray?"
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(backUpOnCloud: Boolean, privateKeyFile: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_BU_AVAILABLE, backUpOnCloud)
                    //putString(getString(R.string.ARG_PUB_KEY), publicKeyFile)
                    putString(getString(R.string.ARG_PRIV_KEY), privateKeyFile)
                }
            }
    }


}