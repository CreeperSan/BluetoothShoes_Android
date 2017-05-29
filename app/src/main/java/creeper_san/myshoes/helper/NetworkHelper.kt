package creeper_san.myshoes.helper

import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest


object NetworkHelper{

    public fun request(requestQueue:RequestQueue,url:String,handler: NetworkHandler){
        val stringRequest:StringRequest = StringRequest(Request.Method.POST,url,
                Response.Listener<String> {
                    response -> handler.onResponse(true,response)
                }
                ,Response.ErrorListener(){
                    handler.onResponse(false,"")
                })
        requestQueue.add(stringRequest)
    }

    public interface NetworkHandler{
        public fun onResponse(isSuccess:Boolean,response:String)
    }

}

