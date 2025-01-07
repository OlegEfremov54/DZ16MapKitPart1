package com.example.dz16mapkitpart1

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log

import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity

import androidx.core.content.ContextCompat

import com.example.dz16mapkitpart1.databinding.ActivityMainBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.Session
import com.yandex.runtime.image.ImageProvider
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.runtime.Error

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var location = Point(53.2122, 50.1438)
    private val zoomValue = 16.5f

    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var placemarkMapObject: PlacemarkMapObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setApiKey(savedInstanceState)
        MapKitFactory.initialize(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lat = intent.getDoubleExtra("lat", location.latitude)
        val lon = intent.getDoubleExtra("lon", location.longitude)
        location = Point(lat, lon)
        binding.mapView.moveToLocation(location, zoomValue)
        setMarkerToLocation(location)


    }

    private fun setApiKey(savedInstanceState: Bundle?) {
        val haveApiKey: Boolean = savedInstanceState?.getBoolean("haveApiKey") ?: false
        if (!haveApiKey)
            MapKitFactory.setApiKey(getString(R.string.may_key))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("haveApiKey", true)
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun MapView.moveToLocation(location: Point, zoom: Float) {
        this.mapWindow.map.move(
            CameraPosition(
                location,
                zoom,
                0f,
                0f
            ),
            Animation(Animation.Type.SMOOTH, 3f),
            null
        )
        val searchOptions = SearchOptions()
        val searchManager = SearchFactory.getInstance()
            .createSearchManager(SearchManagerType.ONLINE)
        searchManager.submit(
            location,
            zoom.toInt(),
            searchOptions,
            object : Session.SearchListener {
                override fun onSearchResponse(p0: Response) {
                    placemarkMapObject.setText(
                        p0.collection.children.first().obj?.name ?: "Нет данных"
                    )
                }

                override fun onSearchError(p0: Error) {
                    Log.d("aaa", "onSearchError: search ERROR")
                }


            }
        )
    }

    private fun setMarkerToLocation(location: Point) {
        val marker = createBitmapFromVector(R.drawable.baseline_place_24)
        mapObjectCollection = binding.mapView.mapWindow.map.mapObjects
        placemarkMapObject = mapObjectCollection.addPlacemark(
            location,
            ImageProvider.fromBitmap(marker)
        )
        placemarkMapObject.opacity = 0.5f
    }


    private fun createBitmapFromVector(art: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(this, art) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}