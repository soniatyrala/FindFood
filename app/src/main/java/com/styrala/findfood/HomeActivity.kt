package com.styrala.findfood

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.styrala.findfood.common.Common.db
import com.styrala.findfood.service.DatabaseService


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        db = DatabaseService(this)

        System.out.println("Connecting database: " + db)

        val buttonMap = findViewById<Button>(R.id.buttonMap)
        buttonMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        val buttonVisited = findViewById<Button>(R.id.buttonHistory)
        buttonVisited.setOnClickListener {
            val intent = Intent(this, VisitedPlacesActivity::class.java)
            startActivity(intent)
        }
    }
}