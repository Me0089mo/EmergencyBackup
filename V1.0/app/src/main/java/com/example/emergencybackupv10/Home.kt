package com.example.emergencybackupv10

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.auth0.android.jwt.JWT
import com.example.emergencybackupv10.fragments.*
import com.example.emergencybackupv10.networking.interfaces.Download
import com.example.emergencybackupv10.networking.interfaces.ServerResponse
import com.example.emergencybackupv10.networking.interfaces.Upload
import com.example.emergencybackupv10.utils.AlertUtils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.nio.CharBuffer


class Home : AppCompatActivity() {
    private val BACKUP_CONFIG = 42
    private val KEY_SELECTION = 43
    private val BACKUP_SELECTION = 44
    private val homeFragment = HomeFragment()
    private val restoreFragment = RestoreFragment()
    private val settingsFragment = SettingsFragment()
    private var backUpOnCloud: Boolean = false;
    private var username: String? = ""
    private var id: String? = ""
    private lateinit var sharedPreferences: SharedPreferences
    private var publicKeyFile: String? = ""
    private var privateKeyFile: Uri? = null
    private var directoryToRestore: String? = null
    private lateinit var  url:String
    private var emergency: Boolean? = null
    private var downloadsDir: String? = null
    private var waitingToUpload = mutableListOf<File>()
    private lateinit var cm: ConnectivityManager

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
        //emergency = intent.getBooleanExtra(getString(R.string.EMERGENCY), false)
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
        if(emergency == true){
            startBackup()
        }
        downloadsDir = applicationContext.filesDir.absolutePath + "/Backup"
    }

    private fun getDataFromIntent() {
        backUpOnCloud = intent.getBooleanExtra(getString(R.string.ARG_BU_AVAILABLE), false)
        username = intent.getStringExtra(getString(R.string.ARG_NAME))
        id = intent.getStringExtra(getString(R.string.ARG_ID))
        publicKeyFile = intent.getStringExtra(getString(R.string.ARG_PUB_KEY))
        emergency = intent.getBooleanExtra(getString(R.string.EMERGENCY), false)
    }

    private fun getDataFromServer() {
        val t = sharedPreferences.getString(getString(R.string.CONFIG_TOKEN), null)
        if (t == null) {
            logOut();
            return;
        }
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
        )

        if (supportFragmentManager.fragments.size == 0  || (supportFragmentManager.fragments[0].tag != tag)){
            supportFragmentManager.beginTransaction().apply {
                add(fragment, tag)
                replace(R.id.nav_host_fragment, fragment)
                addToBackStack(null)
                commit()
            }
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

    fun startBackup() {
        val cipher = AEScfbCipher(this)
        var period_time = sharedPreferences.getInt(getString(R.string.CONFIG_TIME_PERIOD), 0)
        val time: Long = (period_time*24*60*60*1000).toLong()
        val createBackup = Backup(this, getDirList(), cipher, time)
        createBackup.start()
        cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        uploadBackup(File(applicationContext.filesDir.absolutePath, "CipheredData"))
        Log.i("Files waiting", waitingToUpload.size.toString())
        if(waitingToUpload.isNotEmpty()) {
            GlobalScope.launch(Main) { uploadRetarded() }
        }
    }

    fun uploadBackup(files: File) {
        files.listFiles().forEach { file ->
            if(file.isDirectory)
                uploadBackup(file)
            else {
                if(cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected)
                    uploadFile(file)
                else
                    waitingToUpload.add(file)
            }
        }
        //Log.i("debug files=", "done!")
    }

    suspend fun uploadRetarded(){
        return withContext(Dispatchers.IO) {
            while(waitingToUpload.isNotEmpty()){
                if (cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected) {
                    uploadFile(waitingToUpload[0])
                    waitingToUpload.removeAt(0)
                }
            }
        }
    }

    fun downloadBackup(){
        File(downloadsDir).mkdir()
        var files: List<String>? = null
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val downloadService: Download = retrofit.create(Download::class.java)

        val call: Call<ServerResponse> = downloadService.download(
            authorization = sharedPreferences.getString(getString(R.string.CONFIG_TOKEN), null)!!)

        call.enqueue(object : Callback<ServerResponse> {
            override fun onFailure(call: Call<ServerResponse>?, t: Throwable?) {
                Log.i("retrofit fail ", "call fai   led")
            }

            override fun onResponse(call: Call<ServerResponse>?, response: Response<ServerResponse>?) {
                Log.i("retrofit response", response.toString())
                println(response?.body()?.files)
                files = response?.body()?.files!!
                files?.forEach { file -> downloadFile(file) }
                val location = DocumentFile.fromFile(File(downloadsDir)).name
                val restoreFrag : RestoreFragment =
                    supportFragmentManager.findFragmentByTag("restore_frag") as RestoreFragment
                restoreFrag.showBackupPath(location!!)
                directoryToRestore = downloadsDir
                val alertUtils = AlertUtils()
                alertUtils.topToast(applicationContext, "Su respaldo ha terminado de descargarse")
            }
        }
        )
    }

    fun restoreBackup(){
        val rootDir = mutableSetOf<String>()
        directoryToRestore?.let { rootDir.add(it) }
        val decipher = DescifradorAES_CFB(this, privateKeyFile)
        val restoreBackup = Backup(this, rootDir, decipher)
        restoreBackup.start()
    }

    fun uploadFile(file:File) {
        Log.i("debug upload", "uploading ${file.name}")
        val fileURI = Uri.fromFile(file)

        val reqFilePart = RequestBody.create(
                MediaType.parse("file"),
                file
        )

        val multipartFile :MultipartBody.Part = MultipartBody.Part.createFormData(
                "file",
                file.name,
                reqFilePart
        )

        //Create retrofit instance
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        //Get service and call objects
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

    fun downloadFile(fileName: String){
        Log.i("Downloading file", fileName)

        //Create retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //Get service and call objects
        val downloadService : Download = retrofit.create(Download::class.java)

        val call: Call<ResponseBody> = downloadService.downloadFile(
            authorization = sharedPreferences.getString(getString(R.string.CONFIG_TOKEN), null)!!
            ,file = fileName
        )

        call.enqueue(object: Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Log.i("Retrofit fail ", "call failed")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("Retrofit response", response.toString())
                if(response.isSuccessful){
                    GlobalScope.launch(Main) { // launch coroutine in the main thread
                        saveFile(response, fileName)
                    }
                }
            }
        })
    }

    suspend fun saveFile(response: Response<ResponseBody>, fileName: String){
        return withContext(Dispatchers.IO) {
            val file = File(downloadsDir, fileName)
            val fileOutputStream = FileOutputStream(file)
            val inStream = BufferedInputStream(response.body()?.byteStream())
            val data = ByteArray(1024)
            var read = 0;
            while (read != -1) {
                read = inStream.read(data)
                if (read != -1)
                    fileOutputStream.write(data, 0, read)
            }
            fileOutputStream.flush()
            fileOutputStream.close()
            inStream.close()
        }
    }

    override fun onActivityResult(
            requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == BACKUP_CONFIG && resultCode == Activity.RESULT_OK) {
            var dirList = getDirList()
            val num = dirList.size + 1
            resultData?.data?.also { uri ->
                dirList.add("${num}|${uri}")
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
            clear()
            apply()
        }
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}
