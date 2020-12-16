package com.example.emergencybackupv10

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.auth0.android.jwt.JWT
import com.example.emergencybackupv10.fragments.HomeFragment
import com.example.emergencybackupv10.fragments.RestoreFragment
import com.example.emergencybackupv10.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.File


class Home : AppCompatActivity() {
    private var backUpOnCloud: Boolean = false;
    private var username: String? = ""
    private var id: String? = ""
    private val homeFragment = HomeFragment()
    private val restoreFragment = RestoreFragment()
    private val settingsFragment = SettingsFragment()
    private lateinit var cipheredDataPath: String;
    private lateinit var sharedPreferences:SharedPreferences;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val wasLogedIn =intent.getBooleanExtra(getString(R.string.CONFIG_WAS_LOGED_IN), false)

        if(wasLogedIn){
            getDataFromServer()
        }else{
            getDataFromIntent();
        }

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

    private fun getDataFromIntent(){
        backUpOnCloud = intent.getBooleanExtra(getString(R.string.ARG_BU_AVAILABLE), false)
        username = intent.getStringExtra(getString(R.string.ARG_NAME))
        id = intent.getStringExtra(getString(R.string.ARG_ID))
    }

    private fun getDataFromServer(){
        val t = sharedPreferences.getString(getString(R.string.CONFIG_TOKEN),null)!!
        val jwt = JWT(t)
        username = jwt.getClaim(getString(R.string.ARG_NAME)).asString()
        id = jwt.getClaim(getString(R.string.ARG_ID)).asString()
        backUpOnCloud = false
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

    fun logOut(v: View){
        with (sharedPreferences.edit()) {
            remove(getString(com.example.emergencybackupv10.R.string.CONFIG_TOKEN))
            apply()
        }
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}
