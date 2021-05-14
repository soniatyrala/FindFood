package com.styrala.findfood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import com.styrala.findfood.common.Common.addMarkerToMap
import com.styrala.findfood.common.Common.currentResult
import com.styrala.findfood.common.Common.getPhotoUrl
import com.styrala.findfood.common.Common.googleApiService
import com.styrala.findfood.service.IGoogleAPIService
import kotlinx.android.synthetic.main.view_place.*

class ViewPlaceActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mService: IGoogleAPIService
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private var latitude = currentResult.geometry!!.location!!.lat
    private var longitude = currentResult.geometry!!.location!!.lng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_place)
        mService = googleApiService
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_place) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        btn_show_map.setOnClickListener {
//            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentResult.url))
//            startActivity(mapIntent)
//        }

        place_name.text = currentResult.name

        if (currentResult.photos != null){
            Picasso.with(applicationContext)
                .load(getPhotoUrl(currentResult.photos!![0].photo_reference, 1000))
                .into(photo)
        } else {
            Picasso.with(applicationContext)
                .load(currentResult.icon)
                .into(photo)
        }

        place_rating_bar.rating = currentResult.rating.toFloat()
        rating.text = currentResult.rating.toString()
        place_open_hour.text = "Open now: " + (currentResult.opening_hours?.open_now ?: " - ")
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        addMarkerToMap(currentResult, mMap, applicationContext)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude,longitude), 15f))
        mMap.uiSettings.setAllGesturesEnabled(false)
    }
}