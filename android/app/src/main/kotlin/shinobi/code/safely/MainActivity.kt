package shinobi.code.safely

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.util.Log
import androidx.core.app.ServiceCompat
import io.flutter.app.FlutterActivity
import com.github.nisrulz.sensey.Sensey
import com.github.nisrulz.sensey.ShakeDetector
import com.github.nisrulz.sensey.TouchTypeDetector
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(this)

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

        val manager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Sensor:WakeLogTag")
        val screenStateFilter = IntentFilter()
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON)
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(receiver, screenStateFilter)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    val shakeListener: ShakeDetector.ShakeListener = object : ShakeDetector.ShakeListener {
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
            Sensey.getInstance().stopShakeDetection(this)
//            startService(Intent(applicationContext, LocationService::class.java))

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
        Sensey.getInstance().init(applicationContext)
        Log.e("Sensey_Instance", "Sensey Instance created in onStartCommand()")
        return START_STICKY
    }

    inner class ScreenReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent?.action == Intent.ACTION_SCREEN_ON) {
                Sensey.getInstance().stop()
                stopForeground(true)
                stopSelf()
                Log.e("Screen_ON", "Service and Sensey stopped when screen came on!")
            } else if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                startForeground(Process.myPid(), Notification())
                startService(Intent(applicationContext, TheService::class.java))
                Log.e("SCREEN_OFF ::", "RUNNABLE STARTS NEXT! SERVICE HAS STARTED. ")
                val runnable = Runnable {
                    Sensey.getInstance().startShakeDetection(40f, 3000, shakeListener)
//                    Sensey.getInstance()
//                     .startTouchTypeDetection(context, threeFingerSingleTapListener)
                }
                Handler().postDelayed(runnable, 500)
            }
        }
    }
}

