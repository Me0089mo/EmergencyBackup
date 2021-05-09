package com.example.emergencybackupv10.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import com.example.emergencybackupv10.DescifradorAES_CFB
import com.example.emergencybackupv10.R
import java.io.File

/**
 * A fragment representing a list of Items.
 */
class BackupSettings : Fragment() {

    private var columnCount = 1
    private var sharedPreferences : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_backup_settings, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
        // Set the adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        //recyclerView.setHasFixedSize(true);
        if (recyclerView is RecyclerView) {
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                //var dirList = getDirList().toList()
                //Regex.fromLiteral()
                //dirList.forEach { str -> str.dropWhile {  } }
                val testList = mutableListOf<String>()
                for (i in 0..9)
                    testList.add("Pais numero $i")
                adapter = FilesRecyclerViewAdapter(testList)
            }
        }
        return view
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            BackupSettings().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
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

    private fun getDirList(): MutableSet<String> {
        //println("Entra getDirList")
        var dirList = sharedPreferences?.getStringSet(getString(R.string.CONFIG_DIR_SET), null)
        if (dirList == null) {
            dirList = mutableSetOf<String>()
        }
        return dirList;
    }

    private fun saveDirList(dl: MutableSet<String>) {
        //print("Entra saveDirList")
        with(sharedPreferences?.edit()) {
            this?.putStringSet(getString(R.string.CONFIG_DIR_SET), dl)
            this?.apply()
        }
    }

    private fun readDirectory(f: File, decipheredDataPath: String, descifrador: DescifradorAES_CFB){
        if(f.isDirectory){
            f.listFiles()?.forEach { documentFile ->
                if(documentFile.isDirectory) {
                    File(decipheredDataPath, documentFile.name).mkdir()
                    readDirectory(documentFile, "$decipheredDataPath/${documentFile.name}", descifrador)
                }
                else descifrador.decipherFile(documentFile.absolutePath, decipheredDataPath, documentFile.name)
            }
        }
    }
}