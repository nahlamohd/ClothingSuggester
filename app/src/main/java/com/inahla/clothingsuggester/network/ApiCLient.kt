package com.inahla.clothingsuggester.network

import android.util.Log
import com.inahla.clothingsuggester.model.Interval
import com.inahla.clothingsuggester.util.Constants
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class ApiClient (private val utils: NetworkUtils) {
    var intervals = listOf<Interval>()

    private val client: OkHttpClient by lazy {
        val interceptor =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY}
        OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    private val url =
        HttpUrl.Builder().scheme(Constants.SCHEME).host(Constants.BASE_URL)
            .addPathSegment("timeLines").addQueryParameter("apiKey", Constants.API_KEY)
            .addQueryParameter("fields",Constants.FIELDS)
            .addQueryParameter("location", Constants.LOCATION)
            .addQueryParameter("timesteps", Constants.TIME_STEPS).build()

    fun makeRequest(callback: (List<Interval>?, String?) -> Unit) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                callback(null,e.message)
                Log.i("NAHLA", "fail ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string().let{ jsonString ->
                    val jsonArray = utils.getIntervalsJsonArrayFromJson(jsonString!!)
                    intervals= utils.parseIntervals(jsonArray)
                    callback(intervals,null)

                    Log.i("NAHLA", intervals.toString())
                }
            }

        })
    }


}