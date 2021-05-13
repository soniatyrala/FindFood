package com.styrala.findfood

import android.os.Bundle
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

        place_open_hour.text = "Open now: " + (currentResult.opening_hours?.open_now ?: " - ")
    }
}