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
import com.styrala.findfood.common.Common.currentResult
import com.styrala.findfood.common.Common.db
import com.styrala.findfood.common.Common.getPlaceDetailUrl
import com.styrala.findfood.common.Common.googleApiService
import com.styrala.findfood.model.PlaceDetails
import com.styrala.findfood.model.Review
import com.styrala.findfood.service.IGoogleAPIService
import kotlinx.android.synthetic.main.activity_review.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReviewActivity : AppCompatActivity() {

    private lateinit var mService: IGoogleAPIService
    private lateinit var reviews: List<Review>

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        mService = googleApiService
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
                ratingBar.rating.toDouble(),
                editText.text.toString(),
                System.currentTimeMillis().toString(),
                currentResult.place_id.toString()
            )
            db.addReview(review)
            db.addVisitedPlace(currentResult, review)

            Toast.makeText(this.applicationContext, "Review is saved", Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(this@ReviewActivity, ViewPlaceActivity::class.java))
        }

        val linearLayout: View = findViewById(R.id.content)

        // Reviews from database
        if (currentResult.place_id != null) {
            reviews = db.getReviewsByPlaceId(currentResult.place_id!!)
            for(i in reviews.indices) {
                addReview(reviews[i], linearLayout)
            }
        }

        // Reviews from Google API
        if (currentResult.place_id != null) {
            mService.getPlaceDetails(getPlaceDetailUrl(currentResult.place_id!!))
                .enqueue(object : Callback<PlaceDetails> {
                    override fun onResponse(
                        call: Call<PlaceDetails>,
                        response: Response<PlaceDetails>
                    ) {
                        if (response.isSuccessful) {
                            reviews = response.body()!!.result!!.reviews!!
                            currentResult.url = response.body()!!.result!!.url.toString()
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

    @SuppressLint("SetTextI18n")
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
            ratingBar.stepSize = 0.5f
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
            textView.text = "\"" + review.text + "\""
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

