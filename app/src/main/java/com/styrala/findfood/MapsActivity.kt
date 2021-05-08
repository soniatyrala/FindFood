package com.styrala.findfood

import android.Manifest
import android.content.Intent
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
import com.styrala.findfood.common.Common.currentMarkers
import com.styrala.findfood.common.Common.currentPlaces
import com.styrala.findfood.common.Common.currentResult
import com.styrala.findfood.common.Common.googleApiService
import com.styrala.findfood.model.Places
import com.styrala.findfood.model.Results
import com.styrala.findfood.service.BitmapDescriptorService
import com.styrala.findfood.service.IGoogleAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mService: IGoogleAPIService
    private var latitude = 0.0
    private var longitude = 0.0
    private val RESTAURANT_TYPE = "restaurant"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mService = googleApiService
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.clear()
        setCurrentLocationOnMap()
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.setAllGesturesEnabled(true)
        mMap.setOnMarkerClickListener { marker ->
            currentResult = currentPlaces.results!![currentMarkers.indexOf(marker)]
            startActivity(Intent(this@MapsActivity, ViewPlaceActivity::class.java))
            true
        }
    }

    private fun setCurrentLocationOnMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                this.latitude = location?.latitude!!
                this.longitude = location.longitude
                val lastLocation = LatLng(latitude, longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15f))
                mMap.isMyLocationEnabled = true
                getNearByPlaceType()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                44
            )
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

    private fun addMarkerToMap(googlePlace: Results): Marker {
        val lat = googlePlace.geometry!!.location!!.lat
        val lng = googlePlace.geometry!!.location!!.lng
        val location = LatLng(lat, lng)
        val pin = mMap.addMarker(
            MarkerOptions()
                .position(location)
                .title(googlePlace.name)
                .snippet(googlePlace.rating.toString())
                .icon(
                    BitmapDescriptorService.bitmapFromVector(
                        applicationContext, R.drawable.ic_baseline_local_pizza_24
                    )
                )
        )
        pin.showInfoWindow()
        return pin
    }

    private fun getNearByPlaceType() {
        val url = getUrl(this.latitude, this.longitude, RESTAURANT_TYPE)
        mService.getNearbyPlaces(url!!)
            .enqueue(object : Callback<Places> {
                override fun onResponse(call: Call<Places>, response: Response<Places>) {
                    if (response.isSuccessful) {
                        currentPlaces = response.body()!!
                        for (i in currentPlaces.results!!.indices) {
                            currentMarkers.add(addMarkerToMap(currentPlaces.results!![i]))
                        }
                    }
                }

                override fun onFailure(call: Call<Places>, t: Throwable) {
                    Toast.makeText(baseContext, "" + t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}

