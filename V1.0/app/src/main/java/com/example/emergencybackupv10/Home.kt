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

private const val ARG_BU_AVAILABLE = "backUpAvailable"

class Home : AppCompatActivity() {
    private var backUpOnCloud: Boolean = false;
    private val homeFragment = HomeFragment()
    private val restoreFragment = RestoreFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        changeFragment(homeFragment)
        bottom_nav.selectedItemId = R.id.navigation_home
        bottom_nav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_home-> changeFragment(homeFragment)
                R.id.navigation_ajustes-> changeFragment(settingsFragment)
                R.id.navigation_restore-> changeFragment(restoreFragment)
            }
            true
        }

    }

    private fun changeFragment(fragment:Fragment){

//        fragment.arguments = bundleOf(
//            ARG_BU_AVAILABLE to backUpOnCloud
//        )
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment,fragment)
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
            "backUpOnCloud" to backUpOnCloud
        )

        navHostFragment.arguments = bundle
    }

}
