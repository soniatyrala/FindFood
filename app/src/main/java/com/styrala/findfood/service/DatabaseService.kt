package com.styrala.findfood.service

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.styrala.findfood.model.Results
import com.styrala.findfood.model.Review
import com.styrala.findfood.model.VisitedPlace
import java.util.*
import kotlin.collections.ArrayList


class DatabaseService(context: Context?) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_VERSION = 12
        private const val DATABASE_NAME = "FoodDB"
        private const val REVIEW_TABLE = "reviews"
        private const val PLACE_ID = "place_id"
        private const val DATE_TIME = "date_time"
        private const val ID = "id"
        private const val VISITED_TABLE = "visited_places"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val REVIEW_TABLE = ("CREATE TABLE IF NOT EXISTS reviews ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "rating NUMERIC, "
                + "text TEXT, " + "place_id TEXT," + "date_time TEXT)")
        db.execSQL(REVIEW_TABLE)

        val VISITED_TABLE = ("CREATE TABLE IF NOT EXISTS visited_places ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "place_id TEXT," + "name TEXT, "
                + "latitude NUMERIC, " + "longitude NUMERIC," + "text TEXT," + "rating NUMERIC,"
                + "url TEXT," + "date_time TEXT)")
        db.execSQL(VISITED_TABLE)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS $REVIEW_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $VISITED_TABLE")
        onCreate(db)
    }

    fun allVisitedPlaces(): LinkedList<VisitedPlace> {
        val places: LinkedList<VisitedPlace> = LinkedList()
        val query = "SELECT  * FROM $VISITED_TABLE group by $PLACE_ID order by $DATE_TIME desc"
        val db = this.writableDatabase
        val cursor: Cursor = db.rawQuery(query, null)
        var result: VisitedPlace?
        if (cursor.moveToFirst()) {
            do {
                result = VisitedPlace()
                result.place_id = cursor.getString(1)
                result.name = cursor.getString(2)
                result.text_review = cursor.getString(5)
                result.rating_review = cursor.getDouble(6)
                result.url = cursor.getString(7)
                places.add(result)
            } while (cursor.moveToNext())
        }
        return places
    }

    @SuppressLint("Recycle")
    fun getReviewsByPlaceId(place_id: String): LinkedList<Review> {
        val reviews: LinkedList<Review> = LinkedList()
        val query = "SELECT  * FROM $REVIEW_TABLE WHERE $PLACE_ID = '$place_id' order by $ID desc"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val rating = cursor.getDouble(cursor.getColumnIndex("rating"))
                val text = cursor.getString(cursor.getColumnIndex("text"))
                val placeId = cursor.getString(cursor.getColumnIndex("place_id"))
                val time = cursor.getString(cursor.getColumnIndex("date_time"))
                val review = Review(rating, text, time, time, placeId)
                reviews.add(review)
            } while (cursor.moveToNext())
        }
        return reviews
    }

    fun getRatingsByPlaceId(place_id: String): HashMap<String, Double> {
        val ratings: ArrayList<Double> = ArrayList()
        var sumRatings: Double = 0.0
        val result = HashMap<String, Double>()
        val query = "SELECT  * FROM $REVIEW_TABLE WHERE $PLACE_ID = '$place_id'"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val rating = cursor.getDouble(cursor.getColumnIndex("rating"))
                ratings.add(rating)
            } while (cursor.moveToNext())
            for (i in ratings){
                sumRatings += i
            }
            result["amount"] = ratings.size.toDouble()
            result["ratings"] = sumRatings
        }
        return result
    }

    fun addReview(review: Review) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("rating", review.rating)
        values.put("text", review.text)
        values.put("place_id", review.place_id)
        values.put("date_time", review.time)
        // insert
        db.insert(REVIEW_TABLE, null, values)
        Log.d("Saved review: ", review.time.toString())
        db.close()
    }

    fun addVisitedPlace(result: Results, review: Review) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("place_id", result.place_id)
        values.put("name", result.name)
        values.put("latitude", result.geometry!!.location!!.lat)
        values.put("longitude", result.geometry!!.location!!.lng)
        values.put("text", review.text)
        values.put("rating", review.rating)
        values.put("url", result.url)
        values.put("date_time", review.time)
        // insert
        db.insert(VISITED_TABLE, null, values)
        Log.d("Saved place: ", result.name.toString())
        db.close()
    }
}