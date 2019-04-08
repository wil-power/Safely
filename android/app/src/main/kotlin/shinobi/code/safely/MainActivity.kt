package shinobi.code.safely

import android.app.Notification
import android.os.Process
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import io.flutter.app.FlutterActivity
import com.github.nisrulz.sensey.Sensey
import com.github.nisrulz.sensey.ShakeDetector
import io.flutter.plugins.GeneratedPluginRegistrant


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
    val receiver = ScreenReceiver()

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        Sensey.getInstance().init(applicationContext)
        val manager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Sensor:WakeLogTag")

        val screenStateFilter = IntentFilter()
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON)
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(receiver, screenStateFilter)
    }

    val shakeListener = object : ShakeDetector.ShakeListener {
        override fun onShakeDetected() {
            Log.e("SHAKE:::", "Hey I detected a shake event. Sensey rocks!")
        }

        override fun onShakeStopped() {
            Log.e("SHAKESTOPPED:", "Hey I detected that the shake event has stopped. Sensey Rocks!")
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

    inner class ScreenReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("SCREEN_STATE", "OnReceive($intent)")

            if (intent?.action == Intent.ACTION_SCREEN_ON) {
                Log.e("SCREEN_ON", "Screen is on.")
                Sensey.getInstance().stopShakeDetection(shakeListener)
                Log.e("SENSEY_STOPPED: ", "SENSEY STOPPED WHEN SCREEN CAME ON")
            } else if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                val runnable = Runnable {
                    Log.e("RUNNABLE", "Runnable Executing...")
                    Sensey.getInstance().startShakeDetection(shakeListener)
                }
                Handler().postDelayed(runnable, 500)
            }

        }
    }
}