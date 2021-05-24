package com.example.emergencybackupv10

import com.example.emergencybackupv10.networking.interfaces.Upload
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.example.emergencybackupv10.fragments.BackupSettings
import com.example.emergencybackupv10.fragments.HomeFragment
import com.example.emergencybackupv10.fragments.RestoreFragment
import com.example.emergencybackupv10.fragments.SettingsFragment
import com.example.emergencybackupv10.networking.interfaces.ServerResponse
import kotlinx.android.synthetic.main.activity_home.*
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
    private val BACKUP_CONFIG = 42
    private val KEY_SELECTION = 43
    private val BACKUP_SELECTION = 44
    private var backUpOnCloud: Boolean = false;
    private var username: String? = ""
    private var id: String? = ""
    private val homeFragment = HomeFragment()
    private val restoreFragment = RestoreFragment()
    private val settingsFragment = SettingsFragment()
    private lateinit var sharedPreferences: SharedPreferences
    private var publicKeyFile: String? = ""
    private var privateKeyFile: Uri? = null
    private var directoryToRestore: String? = null
    private lateinit var  url:String

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
        changeFragment(homeFragment, "home_frag")
        bottom_nav.selectedItemId = R.id.navigation_home
        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> changeFragment(homeFragment, "home_frag")
                R.id.navigation_ajustes -> changeFragment(settingsFragment, "settings_frag")
                R.id.navigation_restore -> changeFragment(restoreFragment, "restore_frag")
            }
            true
        }
        for (mutableEntry in sharedPreferences.all) {
            println("Preference: ${mutableEntry.key} ${mutableEntry.value}")
        }
        val direct = sharedPreferences.getStringSet(getString(R.string.CONFIG_DIR_SET), null)
        directoryToRestore = applicationContext.filesDir.absolutePath + "/CipheredData"
    }

    private fun getDataFromIntent() {
        backUpOnCloud = intent.getBooleanExtra(getString(R.string.ARG_BU_AVAILABLE), false)
        username = intent.getStringExtra(getString(R.string.ARG_NAME))
        id = intent.getStringExtra(getString(R.string.ARG_ID))
        publicKeyFile = intent.getStringExtra(getString(R.string.ARG_PUB_KEY))
        //privateKeyFile = intent.getStringExtra(getString(R.string.ARG_PRIV_KEY))
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

    private fun changeFragment(fragment: Fragment, tag: String) {
        fragment.arguments = bundleOf(
                R.string.ARG_BU_AVAILABLE.toString() to backUpOnCloud,
                R.string.ARG_NAME.toString() to username,
                R.string.ARG_ID.toString() to id,
                getString(R.string.ARG_PUB_KEY) to publicKeyFile
                //getString(R.string.ARG_PRIV_KEY) to privateKeyFile
        )

        supportFragmentManager.beginTransaction().apply {
            add(fragment, tag)
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

    internal fun intentForBackupConfiguration(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivityForResult(intent, BACKUP_CONFIG)
    }

    internal fun intentForKeySelection(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, KEY_SELECTION)
    }

    internal fun intentForBackupSelection(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivityForResult(intent, BACKUP_SELECTION)
    }

    internal fun getDirList(): MutableSet<String> {
        var dirList = sharedPreferences.getStringSet(getString(R.string.CONFIG_DIR_SET), null)
        if (dirList == null) {
            dirList = mutableSetOf<String>()
        }
        return dirList;
    }

    internal fun resetDirList() {
        with(sharedPreferences.edit()) {
            putStringSet(getString(R.string.CONFIG_DIR_SET), null)
            apply()
        }
    }

    internal fun saveDirList(dl: MutableSet<String>) {
        with(sharedPreferences.edit()) {
            putStringSet(getString(R.string.CONFIG_DIR_SET), dl)
            apply()
        }
    }

    public fun startBackup(v: View) {
        val cipher = AEScfbCipher(this)
        val createBackup = Backup(this, getDirList(), cipher)
        createBackup.start()
    }

    public fun uploadBackup(v: View) {
        val cipherDataFile = File(applicationContext.filesDir.absolutePath, "CipheredData")
        cipherDataFile.listFiles().forEach { file ->
            uploadFile(file)
        }
        Log.i("debug files=", "done!")
    }

    public fun restoreBackup(v: View){
        //Obteniendo carpeta de directorios
        var rootDir = mutableListOf<String>()
        directoryToRestore?.let { rootDir.add(it) }
        val decipher = DescifradorAES_CFB(this, privateKeyFile)
        val restoreBackup = Backup(this, rootDir.toMutableSet(), decipher)
        restoreBackup.start()
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


//        Create retrofit instance
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

//      Get service and call objectts

        val uploadService : Upload = retrofit.create(Upload::class.java)

        val call: Call<ServerResponse> = uploadService.upload(
            authorization = sharedPreferences.getString(getString(R.string.CONFIG_TOKEN), null)!!
            ,file = multipartFile
        )

        call.enqueue(object : Callback<ServerResponse> {
            override fun onFailure(call: Call<ServerResponse>?, t: Throwable?) {
                Log.i("retrofit fail ", "call fai   led")
            }

            override fun onResponse(call: Call<ServerResponse>?, response: Response<ServerResponse>?) {
                Log.i("retrofit response", response.toString())
            }
        }
        )
    }

    override fun onActivityResult(
            requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == BACKUP_CONFIG && resultCode == Activity.RESULT_OK) {
            var dirList = getDirList()
            resultData?.data?.also { uri ->
                dirList.add(uri.toString())
                saveDirList(dirList)
            }
            val backupFrag : BackupSettings =
                supportFragmentManager.findFragmentByTag("backup_config_frag") as BackupSettings
            backupFrag.updateRecycler()
        }
        if(requestCode == KEY_SELECTION && resultCode == Activity.RESULT_OK){
            var location: String = ""
            resultData?.data?.also { uri ->
                privateKeyFile = uri
                location = DocumentFile.fromSingleUri(applicationContext, uri)?.name!!
            }
            val restoreFrag : RestoreFragment =
                supportFragmentManager.findFragmentByTag("restore_frag") as RestoreFragment
            restoreFrag.showKeyPath(location)
        }
        if(requestCode == BACKUP_SELECTION && resultCode == Activity.RESULT_OK){
            var location: String = ""
            resultData?.data?.also { uri ->
                directoryToRestore = uri.toString()
                location = DocumentFile.fromTreeUri(applicationContext, uri)?.name!!
            }
            val restoreFrag : RestoreFragment =
                supportFragmentManager.findFragmentByTag("restore_frag") as RestoreFragment
            restoreFrag.showBackupPath(location)
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
