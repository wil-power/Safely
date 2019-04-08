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

        }

        override fun onShakeStopped() {
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

            if (intent?.action == Intent.ACTION_SCREEN_ON) {
                Sensey.getInstance().stopShakeDetection(shakeListener)
            } else if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                val runnable = Runnable {
                    Sensey.getInstance().startShakeDetection(shakeListener)
                }
                Handler().postDelayed(runnable, 500)
            }

        }
    }
}