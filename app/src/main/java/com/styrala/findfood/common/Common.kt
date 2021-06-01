package com.styrala.findfood.common

import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.styrala.findfood.R
import com.styrala.findfood.model.PlaceDetails
import com.styrala.findfood.model.Places
import com.styrala.findfood.model.Results
import com.styrala.findfood.service.BitmapDescriptorService
import com.styrala.findfood.service.DatabaseService
import com.styrala.findfood.service.IGoogleAPIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Common {
    val MAPS_URL = "https://maps.googleapis.com/"
    val API_KEY = "AIzaSyAo98VzOt144DOb5XuQMSRG1xuWDKWIzVs"
    val RESTAURANT_TYPE = "restaurant"
    lateinit var currentResult: Results
    lateinit var currentPlaces: Places
    lateinit var db: DatabaseService
    var currentMarkers: MutableList<Marker> = mutableListOf()
    var defaultLocation: LatLng = LatLng(52.222727, 21.014003)

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

    fun getPhotoUrl(photoReference: String?, maxWidth: Int): String {
        val placePhotoUrl = StringBuilder(MAPS_URL)
        placePhotoUrl.append("maps/api/place/photo?")
        placePhotoUrl.append("maxwidth=$maxWidth")
        placePhotoUrl.append("&photoreference=$photoReference")
        placePhotoUrl.append("&key=$API_KEY")
        Log.d("Photo URL:  ", placePhotoUrl.toString())
        return placePhotoUrl.toString()
    }

    fun getPlaceDetailUrl(placeId: String?): String {
        val placeDetailsUrl = StringBuilder(MAPS_URL)
        placeDetailsUrl.append("maps/api/place/details/json?")
        placeDetailsUrl.append("place_id=$placeId")
        placeDetailsUrl.append("&key=$API_KEY")
        Log.d("Place details URL:  ", placeDetailsUrl.toString())
        return placeDetailsUrl.toString()
    }

    fun addMarkerToMap(googlePlace: Results, mMap: GoogleMap, applicationContext: Context): Marker {
        val lat = googlePlace.geometry!!.location!!.lat
        val lng = googlePlace.geometry!!.location!!.lng
        val location = LatLng(lat, lng)
        return mMap.addMarker(
            MarkerOptions()
                .position(location)
                .title(googlePlace.name)
                .icon(
                    BitmapDescriptorService.bitmapFromVector(
                        applicationContext, R.drawable.ic_twotone_local_pizza_24
                    )
                )
        )
    }

}