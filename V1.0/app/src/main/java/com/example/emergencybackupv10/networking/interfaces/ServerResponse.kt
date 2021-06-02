package com.example.emergencybackupv10.networking.interfaces

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.sql.Blob

data class ServerResponse (
    val error: Boolean,
    val message: String){

    @SerializedName("files")
    var files : List<String>? = null
}
