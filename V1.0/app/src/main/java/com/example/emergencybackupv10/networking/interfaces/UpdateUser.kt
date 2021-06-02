package com.example.emergencybackupv10.networking.interfaces

import retrofit2.Call
import retrofit2.http.*

interface UpdateUser {
    @FormUrlEncoded
    @PUT("/api/users/update_password")
    fun update_password(
        @Header("authorization") auth :String,
        @Field("password") old_password:String,
        @Field("new_password") new_password:String
    ):Call<ServerResponse>

    @FormUrlEncoded
    @PUT("/api/users/update_email")
    fun update_email(
        @Header("authorization") auth :String,
        @Field("email") new_email:String
    ):Call<ServerResponse>

    @FormUrlEncoded
    @PUT("/api/users/update_key")
    fun update_key(
        @Header("authorization") auth :String,
        @Field("password") password :String,
        @Field("pub_key") new_key:String
    ):Call<ServerResponse>

}