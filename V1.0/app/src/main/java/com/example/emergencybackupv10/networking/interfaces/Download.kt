package com.example.emergencybackupv10.networking.interfaces

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Streaming
import java.util.*

interface Download {
    @GET("/api/download")
    fun download(@Header("authorization") authorization: String): Call<ServerResponse>

    @GET("api/download/{file}")
    @Streaming
    fun downloadFile(
        @Header("authorization") authorization: String,
        @Path("file") file: String): Call<ResponseBody>
}