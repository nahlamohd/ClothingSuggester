package com.inahla.clothingsuggester.network

import android.util.Log
import com.inahla.clothingsuggester.model.Interval
import com.inahla.clothingsuggester.util.Constants
import com.inahla.clothingsuggester.util.Constants.APIKEY
import com.inahla.clothingsuggester.util.Constants.BASE_URL
import com.inahla.clothingsuggester.util.Constants.CELSIUS_UNITS
import com.inahla.clothingsuggester.util.Constants.FIELDS
import com.inahla.clothingsuggester.util.Constants.FIELDS_VALUES
import com.inahla.clothingsuggester.util.Constants.HTTPS_SCHEME
import com.inahla.clothingsuggester.util.Constants.LOCATION
import com.inahla.clothingsuggester.util.Constants.ONE_DAY_TIME_STEPS
import com.inahla.clothingsuggester.util.Constants.TANTA_LOCATION
import com.inahla.clothingsuggester.util.Constants.TIME_LINES
import com.inahla.clothingsuggester.util.Constants.TIME_STEPS
import com.inahla.clothingsuggester.util.Constants.UNITS
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class ApiClient(private val utils: NetworkUtils) {
    var intervals = listOf<Interval>()

    private val client: OkHttpClient by lazy {
        val interceptor =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    val url = HttpUrl.Builder()
        .scheme(HTTPS_SCHEME)
        .host(BASE_URL)
        .addPathSegment("v4")
        .addPathSegment(TIME_LINES)
        .addQueryParameter(LOCATION, TANTA_LOCATION)
        .addQueryParameter(FIELDS, FIELDS_VALUES)
        .addQueryParameter(TIME_STEPS, ONE_DAY_TIME_STEPS)
        .addQueryParameter(UNITS, CELSIUS_UNITS)
        .addQueryParameter(APIKEY, "GeQ9oLtdaR1l3N3irTjLBnvBOd6Vw083")
        .build()


    fun makeRequest(callback: (List<Interval>?, String?) -> Unit) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e.message)
                Log.i("NAHLA", "fail ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string().let { jsonString ->
                    val jsonArray = utils.getIntervalsJsonArrayFromJson(jsonString!!)
                    intervals = utils.parseIntervals(jsonArray)
                    callback(intervals, null)

                    Log.i("NAHLA", intervals.toString())
                }
            }

        })
    }


}