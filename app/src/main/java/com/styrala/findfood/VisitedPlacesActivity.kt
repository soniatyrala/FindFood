package com.styrala.findfood

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.styrala.findfood.common.Common.db
import com.styrala.findfood.model.VisitedPlace

class VisitedPlacesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visited_places)

        val linearLayout: View = findViewById(R.id.content_visited_places)

        //All reviews from database
        for (place in db.allVisitedPlaces()) {
            addPlace(place, linearLayout)
        }
    }

    @SuppressLint("SetTextI18n")
    fun addPlace(place: VisitedPlace, linearLayout: View) {
        val placesLayout = LinearLayout(this)
        placesLayout.orientation = LinearLayout.VERTICAL
        placesLayout.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        placesLayout.setPadding(50, 50, 50, 50)
        (linearLayout as LinearLayout).addView(placesLayout)

        if (place.name != null) {
            val placeName = TextView(this)
            placeName.text = place.name
            placeName.isClickable = true
            placeName.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(place.url)))
            }
            placeName.textSize = 20f
            placeName.setPadding(10, 0, 0, 20)
            placeName.gravity = Gravity.LEFT
            placeName.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            placesLayout.addView(placeName)
        }

        if (place.text_review != null) {
            val textView = TextView(this)
            textView.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            textView.setPadding(20, 20, 0, 20)
            textView.text = "\"" + place.text_review + "\""
            placesLayout.addView(textView)
        }

        val rating = LinearLayout(this)
        rating.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rating.orientation = LinearLayout.HORIZONTAL
        rating.gravity = Gravity.CENTER_VERTICAL
        rating.setPadding(20, 0, 0, 20)

        if (place.rating_review != null) {
            val ratingBar = RatingBar(this)
            ratingBar.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            ratingBar.max = 5
            ratingBar.setIsIndicator(true)
            ratingBar.stepSize = 0.5f
            ratingBar.rating = place.rating_review!!.toFloat()
            val stars = ratingBar.progressDrawable as LayerDrawable
            stars.getDrawable(2)
                .setColorFilter(Color.parseColor("#FF9800"), PorterDuff.Mode.SRC_ATOP)
            val ratingText = TextView(this)
            ratingText.text = place.rating_review.toString()
            ratingText.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            rating.addView(ratingText)
            rating.addView(ratingBar)
            placesLayout.addView(rating)
        }

    }
}