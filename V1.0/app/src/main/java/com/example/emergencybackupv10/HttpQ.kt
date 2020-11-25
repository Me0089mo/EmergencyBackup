import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.ServerError
import com.android.volley.VolleyError
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

    fun onErrorResponse(error: VolleyError) {
        // As of f605da3 the following should work
        val response = error.networkResponse
        if (error is ServerError && response != null) {
            try {
                val res = ""
//                val res = String(
//                    response.data,
////                    HttpHeaderParser.parseCharset(response.headers, "utf-8")
//                )
                // Now you can use any deserializer to make sense of data
                val obj = JSONObject(res)
            } catch (e1: UnsupportedEncodingException) {
                // Couldn't properly decode data to string
                e1.printStackTrace()
            } catch (e2: JSONException) {
                // returned data is not JSONObject?
                e2.printStackTrace()
            }
        }
    }


}