package com.styrala.findfood

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup.LayoutParams
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.styrala.findfood.common.Common.currentResult
import com.styrala.findfood.common.Common.db
import com.styrala.findfood.common.Common.getPlaceDetailUrl
import com.styrala.findfood.common.Common.googleApiService
import com.styrala.findfood.model.PlaceDetails
import com.styrala.findfood.model.Review
import com.styrala.findfood.service.IGoogleAPIService
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.view_place.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ReviewActivity : AppCompatActivity() {

    private lateinit var mService: IGoogleAPIService
    private lateinit var reviews: List<Review>

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        mService = googleApiService
        val appBar = findViewById<View>(R.id.app_bar) as AppBarLayout
        val ratingBar = findViewById<View>(R.id.your_rating) as RatingBar
        val editText = findViewById<View>(R.id.your_review) as EditText
        editText.setOnTouchListener(OnTouchListener { v, event ->
            editText.setFocusable(true)
            editText.setFocusableInTouchMode(true)
            editText.requestFocus()
            editText.text.clear()
            false
        })

        fab.setOnClickListener { view ->
            val review = Review(
                UUID.randomUUID().toString(),
                ratingBar.numStars.toDouble(),
                editText.text.toString(),
                System.currentTimeMillis().toString(),
                currentResult.place_id.toString()
            )
            db.addReview(review)
            db.addVisitedPlace(currentResult, review)
            // dodac review do listy zeby sie wyswietlilo w scrollu

            Toast.makeText(this.applicationContext, "Review is saved", Toast.LENGTH_SHORT)
                .show()
            finish()
        }

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

        if (review.rating != null) {
            val ratingBar = RatingBar(this)
            ratingBar.layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            ratingBar.max = 5
            ratingBar.rating = review.rating.toFloat()
            val stars = ratingBar.progressDrawable as LayerDrawable
            stars.getDrawable(2)
                .setColorFilter(Color.parseColor("#FF9800"), PorterDuff.Mode.SRC_ATOP)
            val ratingText = TextView(this)
            ratingText.text = review.rating.toString()
            ratingText.layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            rating.addView(ratingText)
            rating.addView(ratingBar)
            reviewLayout.addView(rating)
        }

        if (review.text != null) {
            val textView = TextView(this)
            textView.layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            textView.text = review.text
            reviewLayout.addView(textView)
        }

        if (review.author_name != null) {
            val authorView = TextView(this)
            authorView.text = review.author_name
            authorView.gravity = Gravity.RIGHT
            authorView.layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            reviewLayout.addView(authorView)
        }
    }
}

