package com.example.emergencybackupv10

import Upload
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.auth0.android.jwt.JWT
import com.example.emergencybackupv10.fragments.HomeFragment
import com.example.emergencybackupv10.fragments.RestoreFragment
import com.example.emergencybackupv10.fragments.SettingsFragment
import com.example.emergencybackupv10.networking.UploadResponse
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URI


class Home : AppCompatActivity() {
    private var backUpOnCloud: Boolean = false;
    private var username: String? = ""
    private var id: String? = ""
    private val homeFragment = HomeFragment()
    private val restoreFragment = RestoreFragment()
    private val settingsFragment = SettingsFragment()
    private lateinit var sharedPreferences: SharedPreferences
    private var publicKeyFile: String? = ""
    private var privateKeyFile: String? = ""
    private var cipheredDataPath: String? = null
    private var decipheredDataPath: String? = null
    private lateinit var  url:String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        url = getString(R.string.host_url)
        val wasLogedIn = intent.getBooleanExtra(getString(R.string.CONFIG_WAS_LOGED_IN), false)
        if (wasLogedIn) {
            getDataFromServer()
        } else {
            getDataFromIntent()
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
        for (mutableEntry in sharedPreferences.all) {
            println("Preference: ${mutableEntry.key} ${mutableEntry.value}")
        }
        val direct = sharedPreferences.getStringSet(getString(R.string.CONFIG_DIR_SET), null)
    }

    private fun getDataFromIntent() {
        backUpOnCloud = intent.getBooleanExtra(getString(R.string.ARG_BU_AVAILABLE), false)
        username = intent.getStringExtra(getString(R.string.ARG_NAME))
        id = intent.getStringExtra(getString(R.string.ARG_ID))
        publicKeyFile = intent.getStringExtra(getString(R.string.ARG_PUB_KEY))
        privateKeyFile = intent.getStringExtra(getString(R.string.ARG_PRIV_KEY))
    }

    private fun getDataFromServer() {
        val t = sharedPreferences.getString(getString(R.string.CONFIG_TOKEN), null)
        if (t == null) {
            logOut();
            return;
        }
        Log.i("debug", t.toString())
        val jwt = JWT(t)
        username = jwt.getClaim(getString(R.string.ARG_NAME)).asString()
        id = jwt.getClaim(getString(R.string.ARG_ID)).asString()
        backUpOnCloud = false
    }

    private fun changeFragment(fragment: Fragment) {
        fragment.arguments = bundleOf(
                R.string.ARG_BU_AVAILABLE.toString() to backUpOnCloud,
                R.string.ARG_NAME.toString() to username,
                R.string.ARG_ID.toString() to id,
                getString(R.string.ARG_PUB_KEY) to publicKeyFile,
                getString(R.string.ARG_PRIV_KEY) to privateKeyFile
        )

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment, fragment)
            commit()
        }
    }

    //Check if there's any back up in the server
    private fun checkForBackUp() {
        if (true) {
            backUpOnCloud = true
        }
        backUpOnCloud = false
    }

    internal fun addFolderToBackup(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivityForResult(intent, 42)
    }

    internal fun getDirList(): MutableSet<String> {
        var dirList = sharedPreferences.getStringSet(getString(R.string.CONFIG_DIR_SET), null)
        if (dirList == null) {
            dirList = mutableSetOf<String>()
        }
        return dirList;
    }

    private fun saveDirList(dl: MutableSet<String>) {
        with(sharedPreferences.edit()) {
            putStringSet(getString(R.string.CONFIG_DIR_SET), dl)
            apply()
        }
    }

    public fun pickDir(v: View) {
        val cipherDataDirectory = File(this.filesDir, "CipheredData")
        val decipheredDataDirectory = File(this.filesDir, "DecipheredData")
        if (decipheredDataDirectory.exists() || cipherDataDirectory.mkdir())
            cipheredDataPath = cipherDataDirectory.absolutePath
        if (decipheredDataDirectory.exists() || decipheredDataDirectory.mkdir())
            decipheredDataPath = decipheredDataDirectory.absolutePath

        //Creating document picker
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivityForResult(intent, 42)
    }

    public fun startBackup(v: View) {
        val createBackup = Backup(this, getDirList())
        createBackup.create()
    }

    public fun uploadBackup(v: View) {
        val cipherDataFile = File(applicationContext.filesDir.absolutePath, "CipheredData")
        cipherDataFile.listFiles().forEach { file ->
            uploadFile(file)
        }
        Log.i("debug files=", "done!")
    }

    public fun uploadFile(file:File) {
        Log.i("debug upload", "uploading ${file.name}")
        val fileURI = Uri.fromFile(file)
        Log.i("debug upload", "URI: ${fileURI.toString()}")

        val reqFilePart = RequestBody.create(
                MediaType.parse("file"),
                file
        )

        val multipartFile :MultipartBody.Part = MultipartBody.Part.createFormData(
                "file",
                file.name,
                reqFilePart
        )

        val authPart :RequestBody = RequestBody.create(
                MultipartBody.FORM,
                sharedPreferences.getString(getString(R.string.CONFIG_TOKEN), null)!!
        )

//        Create retrofit instance
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

//      Get service and call objectts

        val uploadService : Upload = retrofit.create(Upload::class.java)

        val call: Call<UploadResponse> = uploadService.upload(
                authPart,
                multipartFile
        )

        call.enqueue(object : Callback<UploadResponse> {
            override fun onFailure(call: Call<UploadResponse>?, t: Throwable?) {
                Log.i("retrofit fail ", "call failed")
            }

            override fun onResponse(call: Call<UploadResponse>?, response: Response<UploadResponse>?) {
                Log.i("retrofit response", response.toString())
            }
        }
        );
    }


    public fun decipherData(v: View) {
        val decipher = DescifradorAES_CFB(this, privateKeyFile!!)
        decipher.recoverKeys()
        val cipheredFiles = File(cipheredDataPath!!)
        readDirectory(cipheredFiles, decipheredDataPath!!, decipher)
    }

    private fun readDirectory(f: File, decipheredDataPath: String, descifrador: DescifradorAES_CFB) {
        if (f.isDirectory) {
            f.listFiles()?.forEach { documentFile ->
                if (documentFile.isDirectory) {
                    File(decipheredDataPath, documentFile.name).mkdir()
                    readDirectory(documentFile, "$decipheredDataPath/${documentFile.name}", descifrador)
                } else descifrador.decipherFile(documentFile.absolutePath, decipheredDataPath, documentFile.name)
            }
        }
    }


    override fun onActivityResult(
            requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        var dirList = getDirList()
        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                dirList.add(uri.toString())
                saveDirList(dirList)
            }
        }
    }

    fun logOut(v: View) {
        with(sharedPreferences.edit()) {
            remove(getString(com.example.emergencybackupv10.R.string.CONFIG_TOKEN))
            apply()
        }
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    private fun logOut() {
        with(sharedPreferences.edit()) {
            remove(getString(com.example.emergencybackupv10.R.string.CONFIG_TOKEN))
            apply()
        }
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}
