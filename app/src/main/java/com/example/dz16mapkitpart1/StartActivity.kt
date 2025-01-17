package com.example.dz16mapkitpart1

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.location.Location
import android.location.LocationManager
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartActivity : AppCompatActivity() {

    private var location: Location? = null
    private var locationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

    if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
    ) {
        val permissionsLocation = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        permissionsRequest.launch(permissionsLocation)
    } else {
        getCurrentLocation()
    }



    GlobalScope.launch(Dispatchers.Main) {
        delay(5000)
        val intent = Intent(this@StartActivity, MainActivity::class.java)
            .apply {
                putExtras(bundleOf("lat" to location?.latitude, "lon" to location?.longitude))
            }
        startActivity(intent)
        finish()
    }


}

private val permissionsRequest = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
) { result ->
    var allAreGranted = true
    for (isGranted in result.values) {
        allAreGranted = allAreGranted && isGranted
    }
    if (allAreGranted) {
        Toast.makeText(this@StartActivity, "Разрешения предоставлены", Toast.LENGTH_LONG)
        getCurrentLocation()
    } else {
        Toast.makeText(this@StartActivity, "В разрешениях отказано", Toast.LENGTH_LONG)
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation() {
    locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
    locationManager!!.requestLocationUpdates(
        LocationManager.NETWORK_PROVIDER,
        500,
        200f,
    ) {
        location = it
        Log.d("aaa", "getCurrentLocation: ${it.longitude} , ${it.latitude}")
    }
}

}
