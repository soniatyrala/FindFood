package com.styrala.findfood.model

import android.content.Context
import android.view.View
import android.view.View.inflate
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.styrala.findfood.R


class MyInfoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter {

    private var mWindow = inflate(context, R.layout.info_window, null)

    private fun renderWindowText(marker: Marker, view: View){

        val tvTitle = view.findViewById<TextView>(R.id.title)
        val tvSnippet = view.findViewById<TextView>(R.id.snippet)

        tvTitle.text = marker.title
        tvSnippet.text = marker.snippet
    }

    override fun getInfoContents(marker: Marker): View {
        renderWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        renderWindowText(marker, mWindow)
        return mWindow
    }
}
