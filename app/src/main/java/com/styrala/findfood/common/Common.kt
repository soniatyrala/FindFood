package com.styrala.findfood.common

import com.google.android.gms.maps.model.Marker
import com.styrala.findfood.model.Places
import com.styrala.findfood.model.Results
import com.styrala.findfood.service.IGoogleAPIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Common {

    private val GOOGLE_API_URL = "https://maps.googleapis.com/"
    lateinit var currentResult: Results
    lateinit var currentPlaces: Places
    var currentMarkers: MutableList<Marker> = mutableListOf()

    val googleApiService: IGoogleAPIService
        get() = getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)

    private fun getClient(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}