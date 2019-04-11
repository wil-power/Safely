package shinobi.code.safely

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.*
import android.telephony.SmsManager
import android.util.Log
import io.flutter.app.FlutterActivity
import com.github.nisrulz.sensey.Sensey
import com.github.nisrulz.sensey.ShakeDetector
import com.github.nisrulz.sensey.TouchTypeDetector
import io.flutter.plugins.GeneratedPluginRegistrant
import java.lang.Exception


class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(this)
        Sensey.getInstance().init(applicationContext)
        Log.e("SENSE:", "Sensey Instance created.")

    }

    override fun onStop() {
        super.onStop()
        startService(Intent(this, TheService::class.java))
    }

    override fun onResume() {
        super.onResume()
        stopService(Intent(this, TheService::class.java))
    }
}

class TheService : Service() {
    private val receiver = ScreenReceiver()
    private var wakeLock: PowerManager.WakeLock? = null
    lateinit var vibrator: Vibrator


    override fun onCreate() {
        super.onCreate()
//        Sensey.getInstance().init(applicationContext)
        val manager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Sensor:WakeLogTag")
        val screenStateFilter = IntentFilter()
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON)
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(receiver, screenStateFilter)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    }

    val shakeListener = object : ShakeDetector.ShakeListener {
        override fun onShakeDetected() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        1000,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            else
                vibrator.vibrate(1000)
        }

        override fun onShakeStopped() {
            Log.e(
                "SHAKESTOPPED::::",
                "Shaking has stopped. I think we can all agree that Sensey rocks!"
            )
            sendTextMessage()
//            Sensey.getInstance()
//                .startTouchTypeDetection(applicationContext, threeFingerSingleTapListener)
//            Log.e("DETECTION::::", "Three fingers detection started!")
        }
    }

    val threeFingerSingleTapListener = object : TouchTypeDetector.TouchTypListener {
        override fun onSwipe(p0: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onSingleTap() {
            Log.e("SINGLE_TAP:::", "Single tap detected!")
        }

        override fun onScroll(p0: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onLongPress() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onTwoFingerSingleTap() {
            Log.e("TWOFINGERS_TAP:::", "Two fingers single tap detected!")
        }

        override fun onDoubleTap() {
            Log.e("DOUBLE_TAP", "Double tap detected!")
        }

        override fun onThreeFingerSingleTap() {
            Log.e("THREE_FINGERS:::", "Three fingers single tap detected!")
        }
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        if (wakeLock?.isHeld as Boolean)
            wakeLock?.release()
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(Process.myPid(), Notification())
        wakeLock?.acquire(100)

        return START_STICKY
    }

    private fun sendTextMessage() {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                "+233547532641",
                null,
                "This message was sent with love, from Safely. <3",
                null,
                null
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun getLongLat(context: Context) {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

    }

    inner class ScreenReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent?.action == Intent.ACTION_SCREEN_ON) {
                Sensey.getInstance().stop()
            } else if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                val runnable = Runnable {
                    Sensey.getInstance().startShakeDetection(80f, 5000, shakeListener)
                  //  Sensey.getInstance()
                    // .startTouchTypeDetection(context, threeFingerSingleTapListener)
                }
                Handler().postDelayed(runnable, 500)
            }
        }
    }
}
