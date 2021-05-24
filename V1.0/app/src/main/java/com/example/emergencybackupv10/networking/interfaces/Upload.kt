package com.example.emergencybackupv10.networking.interfaces
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Upload {
    @Multipart
    @POST("/api/upload")
    fun upload(
        @Header("authorization") authorization: String,
        @Part file: MultipartBody.Part): Call<ServerResponse>
}
