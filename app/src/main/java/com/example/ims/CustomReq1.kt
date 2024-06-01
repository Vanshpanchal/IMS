package com.example.ims

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject

class CustomReq1(
    url: String,
    private val token: String,
    private val params: Map<String, String>? = null,
    private val body: JSONObject? = null,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : StringRequest(Method.POST, getUrlWithParams(url, params), listener, errorListener) {

    override fun getHeaders(): MutableMap<String, String> {
        val headers = super.getHeaders()
        val modifiedHeaders = headers.toMutableMap()
        modifiedHeaders["Authorization"] = "Bearer $token"
        return modifiedHeaders
    }

    override fun getParams(): Map<String, String>? {
        return params
    }

    override fun getBody(): ByteArray? {
        return body?.toString()?.toByteArray(Charsets.UTF_8)
    }

    override fun getBodyContentType(): String? {
        return "application/json; charset=utf-8"
    }

    companion object {
        private const val PROTOCOL_CHARSET = "utf-8"

        private fun getUrlWithParams(url: String, params: Map<String, String>?): String {
            if (params == null || params.isEmpty()) return url
            val queryString = StringBuilder()
            for ((key, value) in params.entries) {
                queryString.append("&")
                queryString.append(key)
                queryString.append("=")
                queryString.append(value)
            }
            val baseUrl = if (url.contains("?")) "$url&" else "$url?"
            return baseUrl + queryString.toString().substring(1)
        }
    }
}