import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException


class HttpQ constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: HttpQ? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HttpQ(context).also {
                    INSTANCE = it
                }
            }
    }

    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

     fun getErrorMsg (error: VolleyError) : String {
        if (error is NoConnectionError){
            return "No hay conexión"
        }
        if (error is TimeoutError){
            return "No hay respuesta de parte del servidor"
        }
        if(error is AuthFailureError){
            return "Credenciales incorrectas"
        }
        return "Error desconocido, por favor comprueba tus credenciales"
    }

}