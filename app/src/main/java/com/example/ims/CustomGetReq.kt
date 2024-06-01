package com.example.ims

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class CustomGetReq(
    url: String,
    private val token: String,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : StringRequest(Request.Method.GET, url, listener, errorListener) {

    override fun getHeaders(): Map<String, String> {
        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer $token"
        return headers
    }

}