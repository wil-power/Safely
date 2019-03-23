package shinobi.code.safely

import android.annotation.TargetApi
import android.app.Notification
import android.os.Process
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.EventChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {
    private val CHANNEL = "safely.flutter.io/safely"

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

class StartCmServiceAtBootReceiver : BroadcastReceiver(){
    companion object {
        val TAG = StartCmServiceAtBootReceiver.javaClass.simpleName
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e(TAG, "Boot Detected")
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val mIntent = Intent(context, MainActivity::class.java)
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(mIntent)
        }
    }

}

@TargetApi(11)
class TheService : Service(), SensorEventListener {
    val receiver = ScreenReceiver()
    companion object {
        val TAG = TheService::class.java.simpleName
    }
    private var sensorManager: SensorManager? = null
    private var wakeLock: PowerManager.WakeLock? = null

    // REGISTER THIS AS A SENSOR EVENT LISTENER
    fun registerListener() {
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }


    // UNREGISTER THIS AS A SENSOR EVENT LISTENER
    fun unregisterListener(){
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.e("Sensor", "OnAccuracyChanged()")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.e("Sensor", "OnSensorChanged()")
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val manager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Sensor:WakeLogTag")
        registerReceiver(receiver, IntentFilter(Intent.ACTION_SCREEN_OFF))

    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        unregisterListener()

        if (wakeLock?.isHeld as Boolean)
            wakeLock?.release()

        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(Process.myPid(), Notification())
        registerListener()
        wakeLock?.acquire(100)

        return START_STICKY

    }

    inner class ScreenReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("SCREEN_STATE", "OnReceive($intent)")

            if (intent?.action != Intent.ACTION_SCREEN_OFF) return

            val runnable = Runnable {
                Log.e("RUNNABLE", "Runnable Executing...")
                unregisterListener()
                registerListener()
//                startService(Intent(context, TheService::class.java))

            }
            Handler().postDelayed(runnable, 1000)
        }
    }
}
