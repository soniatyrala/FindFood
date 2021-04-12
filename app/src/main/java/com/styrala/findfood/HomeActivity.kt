package com.styrala.findfood

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
    }

    fun openFoodMaps(view: View?) {
        val intent = Intent(this@HomeActivity, MapsActivity::class.java)
        startActivity(intent)
    }

}