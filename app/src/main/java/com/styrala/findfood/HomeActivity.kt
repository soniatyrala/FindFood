package com.styrala.findfood

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        val buttonMap = findViewById<Button>(R.id.buttonMap)
        buttonMap.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        val buttonVisited = findViewById<Button>(R.id.buttonHistory)
        buttonVisited.setOnClickListener{
            val intent = Intent(this, VisitedActivity::class.java)
            startActivity(intent)
        }
    }
}