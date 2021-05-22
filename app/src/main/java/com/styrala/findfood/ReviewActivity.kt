package com.styrala.findfood

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.styrala.findfood.common.Common.currentResult
import com.styrala.findfood.common.Common.getPlaceDetailUrl
import com.styrala.findfood.common.Common.googleApiService
import com.styrala.findfood.model.PlaceDetails
import com.styrala.findfood.model.Review
import com.styrala.findfood.service.IGoogleAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReviewActivity : AppCompatActivity() {

    private lateinit var mService: IGoogleAPIService
    private lateinit var reviews: List<Review>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        mService = googleApiService
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        val linearLayout: View = findViewById(R.id.content)

        if (currentResult.place_id != null) {
            mService.getPlaceDetails(getPlaceDetailUrl(currentResult.place_id!!))
                .enqueue(object : Callback<PlaceDetails> {
                    override fun onResponse(
                        call: Call<PlaceDetails>,
                        response: Response<PlaceDetails>
                    ) {
                        if (response.isSuccessful) {
                            reviews = response.body()!!.result!!.reviews!!
                            for (i in reviews.indices) {
                                addReview(reviews[i], linearLayout)
                            }
                        }
                    }

                    override fun onFailure(call: Call<PlaceDetails>, t: Throwable) {
                        Toast.makeText(baseContext, "" + t.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    fun addReview(review: Review, linearLayout: View) {
        val reviewLayout = LinearLayout(this)
        reviewLayout.orientation = LinearLayout.VERTICAL
        reviewLayout.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        reviewLayout.setPadding(50, 50, 50, 50)
        (linearLayout as LinearLayout).addView(reviewLayout)

        val rating = LinearLayout(this)
        rating.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        rating.orientation = LinearLayout.HORIZONTAL
        rating.gravity = Gravity.CENTER_VERTICAL
        val ratingBar = RatingBar(this)
        ratingBar.max = 5
        ratingBar.rating = review.rating.toFloat()
        ratingBar.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        val stars = ratingBar.progressDrawable as LayerDrawable
        stars.getDrawable(2).setColorFilter(Color.parseColor("#FF9800"), PorterDuff.Mode.SRC_ATOP)
        val ratingText = TextView(this)
        ratingText.text = review.rating.toString()
        ratingText.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        rating.addView(ratingText)
        rating.addView(ratingBar)

        val textView = TextView(this)
        textView.text = review.text
        textView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val authorView = TextView(this)
        authorView.text = review.author_name
        authorView.gravity = Gravity.RIGHT
        authorView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        reviewLayout.addView(rating)
        reviewLayout.addView(textView)
        reviewLayout.addView(authorView)
    }
}

