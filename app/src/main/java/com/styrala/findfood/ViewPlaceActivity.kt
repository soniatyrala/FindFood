package com.styrala.findfood

import android.annotation.SuppressLint
import android.content.Intent
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
import com.styrala.findfood.common.Common.db
import com.styrala.findfood.common.Common.getPhotoUrl
import com.styrala.findfood.common.Common.googleApiService
import com.styrala.findfood.service.IGoogleAPIService
import kotlinx.android.synthetic.main.view_place.*
import kotlin.math.round

class ViewPlaceActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mService: IGoogleAPIService
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private var latitude = currentResult.geometry!!.location!!.lat
    private var longitude = currentResult.geometry!!.location!!.lng

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_place)
        mService = googleApiService
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_place) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btn_opinions.setOnClickListener {
            startActivity(Intent(this@ViewPlaceActivity, ReviewActivity::class.java))
        }

        btn_backToMap.setOnClickListener {
            startActivity(Intent(this@ViewPlaceActivity, MapsActivity::class.java))
        }

        place_name.text = currentResult.name

        if (currentResult.photos != null) {
            Picasso.with(applicationContext)
                .load(getPhotoUrl(currentResult.photos!![0].photo_reference, 1000))
                .into(photo)
        }
        val ratingsFromDB = db.getRatingsByPlaceId(currentResult.place_id.toString())
        if (ratingsFromDB.isNotEmpty()) {
            val ratingFromDB = round((currentResult.rating * currentResult.user_ratings_total).plus(ratingsFromDB["ratings"]!!).div(currentResult.user_ratings_total
                        .plus(ratingsFromDB["amount"]!!.toDouble())) * 100.0) / 100.0
            place_rating_bar.rating = ratingFromDB.toFloat()
            ratings.text = "(" + currentResult.user_ratings_total.plus(ratingsFromDB["amount"]!!.toInt()) + ")"
            rating.text = ratingFromDB.toString()
        } else {
            place_rating_bar.rating = currentResult.rating.toFloat()
            ratings.text = "(" + currentResult.user_ratings_total + ")"
            rating.text = currentResult.rating.toString()
        }
        place_open_hour.text = "Open now: " + (currentResult.opening_hours?.open_now ?: " - ")
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE;
        mMap.clear()
        addMarkerToMap(currentResult, mMap, applicationContext)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 18f))
        mMap.uiSettings.setAllGesturesEnabled(false)
    }
}