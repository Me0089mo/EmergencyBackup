package com.example.emergencybackupv10

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.emergencybackupv10.fragments.HomeFragment
import com.example.emergencybackupv10.fragments.RestoreFragment
import com.example.emergencybackupv10.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_home.*


class Home : AppCompatActivity() {
    private var backUpOnCloud: Boolean = false;
    private var username: String? = ""
    private var id: String? = ""
    private val homeFragment = HomeFragment()
    private val restoreFragment = RestoreFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        backUpOnCloud = intent.getBooleanExtra(R.string.ARG_BU_AVAILABLE.toString(), false)
        username = intent.getStringExtra(R.string.ARG_NAME.toString())
        id = intent.getStringExtra(R.string.ARG_ID.toString())
        changeFragment(homeFragment)
        bottom_nav.selectedItemId = R.id.navigation_home
        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> changeFragment(homeFragment)
                R.id.navigation_ajustes -> changeFragment(settingsFragment)
                R.id.navigation_restore -> changeFragment(restoreFragment)
            }
            true
        }
    }

    private fun changeFragment(fragment: Fragment) {
        fragment.arguments = bundleOf(
            R.string.ARG_BU_AVAILABLE.toString() to backUpOnCloud,
            R.string.ARG_NAME.toString() to username,
            R.string.ARG_ID.toString() to id

        )
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment, fragment)
            commit()
        }

    }

    //Check if theres any back up in the server
    private fun checkForBackUp() {
        if (true) {
            backUpOnCloud = true
        }
        backUpOnCloud = false
    }

    private fun sendDataToFrags(navHostFragment: NavHostFragment) {
        var bundle: Bundle = bundleOf(
            R.string.ARG_BU_AVAILABLE.toString() to backUpOnCloud,
            R.string.ARG_NAME.toString() to username,
            R.string.ARG_ID.toString() to id
        )
        navHostFragment.arguments = bundle
    }

}
