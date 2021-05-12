package com.styrala.findfood.common

import android.util.Log
import com.styrala.findfood.service.IGoogleAPIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Common {
    val MAPS_URL = "https://maps.googleapis.com/"
    val API_KEY = "AIzaSyAo98VzOt144DOb5XuQMSRG1xuWDKWIzVs"
    val RESTAURANT_TYPE = "restaurant"

    val googleApiService: IGoogleAPIService
        get() = getClient(MAPS_URL).create(IGoogleAPIService::class.java)

    private fun getClient(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getUrl(latitude: Double, longitude: Double, placeType: String): String? {
        val googlePlacesUrl = StringBuilder(MAPS_URL)
        googlePlacesUrl.append("maps/api/place/nearbysearch/json?")
        googlePlacesUrl.append("location=$latitude,$longitude")
        googlePlacesUrl.append("&radius=1000")
        googlePlacesUrl.append("&type=$placeType")
        googlePlacesUrl.append("&sensor=true")
        googlePlacesUrl.append("&key=$API_KEY")
        Log.d("URL: ", googlePlacesUrl.toString())
        return googlePlacesUrl.toString()
    }

}