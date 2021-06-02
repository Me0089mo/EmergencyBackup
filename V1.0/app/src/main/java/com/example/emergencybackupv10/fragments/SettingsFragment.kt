package com.example.emergencybackupv10.fragments
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import androidx.preference.PreferenceManager
import com.example.emergencybackupv10.R
import kotlinx.android.synthetic.main.fragment_change_key.*
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {
    private var backUpOnCloud: Boolean? = null
    private var dir_set:Boolean? = null;
    private var username: String? = "DefaultUsernameText"
    private val changeEmailFragment = ChangeEmail()
    private val changePasswordFragment = ChangePassword()
    private val changePubKeyFragment = ChangeKey()
    private val backupSettingsFragment = BackupSettings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            backUpOnCloud = it.getBoolean(R.string.ARG_BU_AVAILABLE.toString())
            dir_set = it.getBoolean(R.string.CONFIG_DIR_SET.toString())
            username = it.getString(R.string.ARG_NAME.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)

    }

    override fun onStart() {
        super.onStart()
        change_mail_button.setOnClickListener { v -> changeFragment(changeEmailFragment, "change_email_frag") }
        change_password_button.setOnClickListener { v -> changeFragment(changePasswordFragment, "change_password_frag")}
        backup_config_button.setOnClickListener { v -> changeFragment(backupSettingsFragment, "backup_config_frag") }
        get_new_key_button.setOnClickListener{v-> changeFragment(changePubKeyFragment,"change_pub_key")}
    }



    companion object {
        @JvmStatic
        fun newInstance(backupOnCloud: Boolean, username:String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(R.string.ARG_BU_AVAILABLE.toString(), backupOnCloud)
                    putString(R.string.ARG_NAME.toString(), username)
                }
            }
    }

    private fun changeFragment(fragment: Fragment, fragmentTag: String) {
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            add(fragment, fragmentTag)
            replace(R.id.nav_host_fragment, fragment)
            addToBackStack(null)
            commit()
        }
    }


}