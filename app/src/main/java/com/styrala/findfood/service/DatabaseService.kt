package com.styrala.findfood.service

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.styrala.findfood.model.Results
import com.styrala.findfood.model.Review
import java.util.*


class DatabaseService(context: Context?) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        val REVIEW_TABLE = ("CREATE TABLE IF NOT EXISTS reviews ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "review_id TEXT, " + "rating NUMERIC, "
                + "text TEXT, " + "place_id TEXT," + "date_time TEXT)")
        db.execSQL(REVIEW_TABLE)

        val VISITED_TABLE = ("CREATE TABLE IF NOT EXISTS visited_places ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "place_id TEXT," + "name TEXT, "
                + "latitude NUMERIC, " + "longitude NUMERIC," + "review_id TEXT)")
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

//    fun allReviews(): List<Review?> {
//        val reviews: LinkedList<Review> = LinkedList<Review>()
//        val query = "SELECT  * FROM $REVIEW_TABLE"
//        val db = this.writableDatabase
//        val cursor: Cursor = db.rawQuery(query, null)
//        var review: Review?
//        if (cursor.moveToFirst()) {
//            do {
//                review = Review()
//                review.rating = cursor.getDouble(1)
//                review.text = cursor.getString(2)
//                review.place_id = cursor.getString(3)
//                review.time = cursor.getLong(4)
//                reviews.add(review)
//            } while (cursor.moveToNext())
//        }
//        return reviews
//    }

    fun addReview(review: Review) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("review_id", review.id)
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
        values.put("review_id", review.id)
        // insert
        db.insert(VISITED_TABLE, null, values)
        Log.d("Saved place: ", result.name.toString())
        db.close()
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "FoodDB_2"
        private const val REVIEW_TABLE = "reviews"
        private const val VISITED_TABLE = "visited_places"
    }
}