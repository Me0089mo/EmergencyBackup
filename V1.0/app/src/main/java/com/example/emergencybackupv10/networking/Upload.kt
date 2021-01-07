
import com.example.emergencybackupv10.networking.Customresponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Upload {
    @Multipart
    @POST("/api/upload")
    fun upload(
        @Header("Authorization") authorization: String?,
        @Part("file") file: RequestBody?,
        @Part("name") fname: RequestBody?,
        @Part("id") id: RequestBody?): Call<Customresponse>
}