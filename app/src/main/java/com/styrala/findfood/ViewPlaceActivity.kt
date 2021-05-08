package com.styrala.findfood

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.styrala.findfood.common.Common.currentResult
import com.styrala.findfood.common.Common.googleApiService
import com.styrala.findfood.model.PlaceDetails
import com.styrala.findfood.service.IGoogleAPIService
import kotlinx.android.synthetic.main.activity_view_place.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewPlaceActivity : AppCompatActivity() {

    private lateinit var mService: IGoogleAPIService
    var place: PlaceDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_place)
        mService = googleApiService

//        btn_show_map.setOnClickListener {
//            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentResult.url))
//            startActivity(mapIntent)
//        }

        if (currentResult.name != null) {
            place_name.text = currentResult.name
        }

        if (currentResult.photos != null && currentResult.photos!!.isNotEmpty())
            Picasso.with(this)
                .load(getPhotoOfPlace(currentResult.photos!![0].photo_reference, 1000))
                .into(photo)

        if (currentResult.rating != null)
            place_rating_bar.rating = currentResult.rating.toFloat()
        else
            place_rating_bar.visibility = View.GONE

        if (currentResult.opening_hours != null)
            place_open_hour.text = "Open now: " + currentResult.opening_hours!!.open_now
        else
            place_open_hour.visibility = View.GONE

        if (currentResult.place_id != null) {
            mService.getPlaceDetails(getPlaceDetailUrl(currentResult.place_id!!))
                .enqueue(object : Callback<PlaceDetails> {
                    override fun onResponse(
                        call: Call<PlaceDetails>,
                        response: Response<PlaceDetails>
                    ) {
                        if (response.isSuccessful) {
                            place = response.body()
//                            tutaj jest blad!
//                            if (place!!.results!!.formatted_address != null)
//                                place_address.text = place!!.results!!.formatted_address
                        }
                    }

                    override fun onFailure(call: Call<PlaceDetails>, t: Throwable) {
                        Toast.makeText(baseContext, "" + t.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }

    }

    private fun getPlaceDetailUrl(placeId: String?): String {
        val placeDetailsUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?")
        placeDetailsUrl.append("placeid=$placeId")
        placeDetailsUrl.append("&key=" + getString(R.string.google_maps_key))
        Log.d("getUrl ", placeDetailsUrl.toString())
        return placeDetailsUrl.toString()
    }

    private fun getPhotoOfPlace(photoReference: String?, maxWidth: Int): String {
        val placePhotoUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/photo?")
        placePhotoUrl.append("maxWidth=$maxWidth")
        placePhotoUrl.append("&photoreference=$photoReference")
        placePhotoUrl.append("&key=" + getString(R.string.google_maps_key))
        Log.d("getUrl ", placePhotoUrl.toString())
        return placePhotoUrl.toString()
    }
}