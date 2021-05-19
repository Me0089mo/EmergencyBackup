package com.example.emergencybackupv10.fragments

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.emergencybackupv10.Home
import com.example.emergencybackupv10.R
import kotlinx.android.synthetic.main.fragment_restore.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RestoreFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restore, container, false)
    }

    override fun onStart() {
        super.onStart()
        btn_select_key.setOnClickListener { v ->
            (activity as Home).intentForKeySelection()
        }
        btn_select_backup.setOnClickListener { v ->
            (activity as Home).intentForBackupSelection()
        }
        btn_download.setOnClickListener { v -> }
        btn_restore.setOnClickListener { v ->
            (activity as Home).restoreBackup(v)
        }
    }

    fun showKeyPath(location: String){
        key_location.setText(location)
    }

    fun showBackupPath(location: String){
        backup_location.setText(location)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RestoreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}