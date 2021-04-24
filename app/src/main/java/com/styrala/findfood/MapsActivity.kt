package com.styrala.findfood

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.styrala.findfood.model.Places
import com.styrala.findfood.model.Results
import com.styrala.findfood.service.BitmapDescriptorService
import com.styrala.findfood.service.IGoogleAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mMarker: Marker
    private lateinit var mService: IGoogleAPIService
    private var latitude = 0.0
    private var longitude = 0.0
    private val MAPS_URL = "https://maps.googleapis.com/"
    private val RESTAURANT_TYPE = "restaurant"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mService = getClient(MAPS_URL).create(IGoogleAPIService::class.java)
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.clear()
        setCurrentLocationOnMap()
    }

    private fun setCurrentLocationOnMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                this.latitude = location?.latitude!!
                this.longitude = location.longitude
                val lastLocation = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                    .position(lastLocation)
                    .title("I'm here!")
                    .icon(BitmapDescriptorService.bitmapFromVector(applicationContext, R.drawable.ic_baseline_person_pin_circle_24))
                mMarker = mMap.addMarker(markerOptions)
                getNearByPlaceType(RESTAURANT_TYPE)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15f))
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                44)
            setCurrentLocationOnMap()
        }
    }

    private fun getUrl(latitude: Double, longitude: Double, placeType: String): String? {
        val googlePlacesUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googlePlacesUrl.append("location=$latitude,$longitude")
        googlePlacesUrl.append("&radius=1000")
        googlePlacesUrl.append("&type=$placeType")
        googlePlacesUrl.append("&sensor=true")
        googlePlacesUrl.append("&key=" + getString(R.string.google_maps_key))
        Log.d("getUrl", googlePlacesUrl.toString())
        return googlePlacesUrl.toString()
    }

    private fun getClient(baseUrl: String): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private fun addMarkerToMap(googlePlace: Results){
        val lat = googlePlace.geometry!!.location!!.lat
        val lng = googlePlace.geometry!!.location!!.lng
        val location = LatLng(lat, lng)
        val pin = mMap.addMarker(
            MarkerOptions()
                .position(location)
                .title(googlePlace.name)
                .icon(BitmapDescriptorService.bitmapFromVector(
                applicationContext, R.drawable.ic_baseline_local_pizza_24)))
        pin.showInfoWindow()
    }

    private fun getNearByPlaceType(placeType: String) {
        val url = getUrl(this.latitude, this.longitude, placeType)
        mService.getNearbyPlaces(url!!)
            .enqueue(object : Callback<Places>{
                override fun onResponse(call: Call<Places>, response: Response<Places>) {
                    if (response.isSuccessful){
                        for(i in response.body()!!.results!!.indices){
                            addMarkerToMap(response.body()!!.results!![i])
                        }
                    }
                }
                override fun onFailure(call: Call<Places>, t: Throwable) {
                    Toast.makeText(baseContext,""+t.message,Toast.LENGTH_SHORT).show()
                }
            })
    }
}

