package com.styrala.findfood

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup.LayoutParams
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.styrala.findfood.common.Common.APP_TAG
import com.styrala.findfood.common.Common.currentResult
import com.styrala.findfood.common.Common.currentReviews
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
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ReviewActivity : AppCompatActivity() {

    private lateinit var mService: IGoogleAPIService
    private var photoFile: File = File("null")

    @SuppressLint("ClickableViewAccessibility", "QueryPermissionsNeeded")
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
            lateinit var review: Review
            if (!photoFile.absolutePath.contains("null")) {
                review = Review(
                    ratingBar.rating.toDouble(),
                    editText.text.toString(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    currentResult.place_id.toString(),
                    photoFile.absolutePath
                )
            } else {
                review = Review(
                    ratingBar.rating.toDouble(),
                    editText.text.toString(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    currentResult.place_id.toString()
                )
            }

            db.addReview(review)
            db.addVisitedPlace(currentResult, review)

            Toast.makeText(this.applicationContext, "Review is saved", Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(this@ReviewActivity, ViewPlaceActivity::class.java))
        }

        val content: LinearLayout = findViewById(R.id.content)

        // Reviews from database
        currentReviews = db.getReviewsByPlaceId(currentResult.place_id!!)

        // Reviews from Google API
        createReviewsFromApi()

        for (i in currentReviews.indices) {
            addReview(currentReviews[i], content)
        }

        sorting_by_date.setOnClickListener {
            content.removeAllViews()
            var reviews = currentReviews as List<Review>
            reviews = reviews.sortedByDescending { it.time }
            for (r in reviews) {
                addReview(r, content)
            }
        }

        sorting_by_best.setOnClickListener {
            content.removeAllViews()
            var reviews = currentReviews as List<Review>
            reviews = reviews.sortedByDescending { it.rating }
            for (r in reviews) {
                addReview(r, content)
            }
        }

        sorting_by_worst.setOnClickListener {
            content.removeAllViews()
            var reviews = currentReviews as List<Review>
            reviews = reviews.sortedBy { it.rating }
            for (r in reviews) {
                addReview(r, content)
            }
        }

        fab_photo.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFileUri(
                LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("ddMMyy_HHmm")) + "_" + currentResult.place_id.toString() + ".jpg"
            )
            val fileProvider: Uri =
                FileProvider.getUriForFile(
                    this@ReviewActivity,
                    "com.codepath.fileprovider.findfood",
                    photoFile!!
                )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, 1034);
            }
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
            textView.setPadding(0, 0, 0, 20)
            reviewLayout.addView(textView)
        }

        if (review.profile_photo_url != null && review.profile_photo_url!!.contains(".jpg")) {
            val image = ImageView(this)
            image.setImageBitmap(BitmapFactory.decodeFile(review.profile_photo_url))
            val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 400)
            image.layoutParams = layoutParams
            reviewLayout.addView(image)
        }

        val desc = LinearLayout(this)
        desc.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        desc.orientation = LinearLayout.HORIZONTAL
        desc.gravity = Gravity.CENTER_VERTICAL
        desc.setPadding(0, 20, 0, 20)

        if (review.relative_time_description != null) {
            val date = TextView(this)
            date.text = review.relative_time_description
            date.setTypeface(null, Typeface.ITALIC)
            date.layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            date.setPadding(0, 0, 300, 0)
            desc.addView(date)
        }

        val authorView = TextView(this)
        authorView.text = review.author_name
        authorView.setTypeface(null, Typeface.BOLD)
        authorView.layoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        desc.addView(authorView)
        reviewLayout.addView(desc)
    }

    private fun createReviewsFromApi() {
        if (currentResult.place_id != null) {
            mService.getPlaceDetails(getPlaceDetailUrl(currentResult.place_id!!))
                .enqueue(object : Callback<PlaceDetails> {
                    override fun onResponse(
                        call: Call<PlaceDetails>,
                        response: Response<PlaceDetails>
                    ) {
                        if (response.isSuccessful) {
                            val reviews = response.body()!!.result!!.reviews
                            currentResult.url = response.body()!!.result!!.url.toString()
                            for (i in reviews!!.indices) {
                                currentReviews.add(reviews[i])
                                addReview(reviews[i], content)
                            }
                        }
                    }

                    override fun onFailure(call: Call<PlaceDetails>, t: Throwable) {
                        Toast.makeText(baseContext, "" + t.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
        }
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1034) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Picture was taken! Please, add review!", Toast.LENGTH_SHORT)
                    .show()
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

