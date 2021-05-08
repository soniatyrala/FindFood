package com.styrala.findfood.service

import com.styrala.findfood.model.PlaceDetails
import com.styrala.findfood.model.Places
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface IGoogleAPIService {

    @GET
    fun getNearbyPlaces(@Url url: String): Call<Places>

    @GET
    fun getPlaceDetails(@Url url: String): Call<PlaceDetails>
}