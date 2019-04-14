package shinobi.code.safely

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.StringBuilder

class LocationService : Service() {
    companion object {
        const val TAG = "MyLocationService"
        const val LOCATION_INTERVAL = 1000L
        const val LOCATION_DISTANCE = 10f
    }

    private var locationManager: LocationManager? = null

    private class LocationListener(provider: String) : android.location.LocationListener {
        var lastLocation: Location

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

        }

        override fun onProviderEnabled(p0: String?) {

        }

        override fun onProviderDisabled(p0: String?) {

        }


        init {
            Log.e(TAG, "LocationListener $provider")
            lastLocation = Location(provider)
        }

        override fun onLocationChanged(location: Location?) {
            Log.e(TAG, "onLocationChanged $location")
            lastLocation.set(location)
            sendTextMessage()

        }

        fun getLongLat(): Pair<Double, Double> {
            return Pair(lastLocation.longitude, lastLocation.latitude)
        }

        private fun sendTextMessage() {
            val (long, lat) = getLongLat()
            val stringLong = long.toString()
            val stringLat = lat.toString()

            val sb = "http://maps.google.com/?q=$stringLong,$stringLat"

//            try {
//                val smsManager = SmsManager.getDefault()
//                smsManager.sendTextMessage(
//                    "+233547532641",
//                    null,
//                    "This message was sent with love, from Safely. <3. Location: $sb" ,
//                    null,
//                    null
//                )
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
           val res = sendSms("+233209050642", sb)
            if (res == 1) {
                print("Sent from background service successfully")
            }else {
                print("An error occurred!")
            }
        }

    }

    private val locationListeners = arrayOf(LocationListener(LocationManager.PASSIVE_PROVIDER))

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")

        initializeLocationManager()

        try {
            locationManager?.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER,
                LOCATION_INTERVAL,
                LOCATION_DISTANCE,
                locationListeners[0]
            )
        } catch (ex: SecurityException) {
            Log.i(TAG, "failed to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null) {
            for (i in 0..locationListeners.size) {
                try {
                    locationManager!!.removeUpdates(locationListeners[i])
                } catch (ex: Exception) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex)
                }
            }
        }
    }

    private fun initializeLocationManager() {
        if (locationManager == null) {
            locationManager =
                applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }
}