package com.example.emergencybackupv10.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.emergencybackupv10.R
import kotlinx.android.synthetic.main.fragment_home.*

private const val ARG_BU_AVAILABLE = "backUpAvailable"

class HomeFragment : Fragment() {

    private var backUpOnCloud: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            backUpOnCloud = it.getBoolean(ARG_BU_AVAILABLE)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onStart() {
        super.onStart()
        setText()
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
        fun newInstance(backUpOnCloud: Boolean) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_BU_AVAILABLE, backUpOnCloud)
                }
            }
    }


}