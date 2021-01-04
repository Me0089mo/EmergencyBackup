package com.example.emergencybackupv10.fragments
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.emergencybackupv10.R
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {
    private var backUpOnCloud: Boolean? = null
    private var dir_set:Boolean? = null;
    private var name: String? = "DefaultUsernameText"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            backUpOnCloud = it.getBoolean(R.string.ARG_BU_AVAILABLE.toString())
            dir_set = it.getBoolean(R.string.CONFIG_DIR_SET.toString())
            name = it.getString(R.string.ARG_NAME.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)

    }

    override fun onStart() {
        super.onStart()
    }

    fun set_dashboard_text(){
        dashboard_username.text=name
        if(dir_set!!){
            dashboard_dir.text = ""
        }else{

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(backupOnCloud: Boolean) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(R.string.ARG_BU_AVAILABLE.toString(), backupOnCloud)
                }
            }
    }
}