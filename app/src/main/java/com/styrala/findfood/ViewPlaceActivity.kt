package com.styrala.findfood

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.styrala.findfood.common.Common.currentResult
import com.styrala.findfood.common.Common.getPhotoUrl
import com.styrala.findfood.common.Common.googleApiService
import com.styrala.findfood.service.IGoogleAPIService
import kotlinx.android.synthetic.main.view_place.*

class ViewPlaceActivity : AppCompatActivity() {

    private lateinit var mService: IGoogleAPIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_place)
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
                .load(getPhotoUrl(currentResult.photos!![0].photoReference, 1000))
                .into(photo)

        if (currentResult.rating != null)
            place_rating_bar.rating = currentResult.rating.toFloat()
        else
            place_rating_bar.visibility = View.GONE

        if (currentResult.openingHours != null)
            place_open_hour.text = "Open now: " + currentResult.openingHours!!.openNow
        else
            place_open_hour.visibility = View.GONE
    }
}