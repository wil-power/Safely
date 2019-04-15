package shinobi.code.safely

import android.app.Notification
import android.app.Service
import android.content.*
import android.os.*
import android.util.Log
import io.flutter.app.FlutterActivity
import com.github.nisrulz.sensey.Sensey
import com.github.nisrulz.sensey.ShakeDetector
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(this)

        MethodChannel(flutterView, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "sendSms") {
                val number = call.argument<String>("phone")
                val message = call.argument<String>("message")
                val numberList = number?.split(',') as ArrayList


                if (!(number.isNullOrBlank() || message.isNullOrBlank())) {
                    val intent = Intent(this, LocationService::class.java)
                    val bundle = Bundle()
                    bundle.putString("message", message)
                    bundle.putStringArrayList("numbers", numberList)
                    intent.putExtras(bundle)
                    startService(intent)

                    result.success("success")
                    val handler = Handler()
                    val runner = Runnable{
                        stopService(intent)
                    }
                    handler.postDelayed(runner, 180_000)
                } else {
                    result.error("Err", "An error occurred!", "")
                }
            } else if(call.method == "persistContactsNatively"){
                val number = call.argument<String>("phone")
                val prefs = getPreferences(Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putString("numbers", number)
                editor.apply()
                result.success("success")
            }else {
                result.notImplemented()
            }
        }

    }


    override fun onStop() {
        super.onStop()
        startService(Intent(this, TheService::class.java))
    }

    companion object {
        val CHANNEL = "io.safely.code.shinobi/sendSms"
    }

}


class TheService : Service() {
    private val receiver = ScreenReceiver()
    private var wakeLock: PowerManager.WakeLock? = null
    lateinit var vibrator: Vibrator

    override fun onCreate() {
        super.onCreate()
        Sensey.getInstance().init(applicationContext)
        Log.e("Sensey_Instance", "Sensey Instance created in Service's onCreate()")

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
                        2000,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            else
                vibrator.vibrate(2000)
        }

        override fun onShakeStopped() {
            Log.e(
                "SHAKESTOPPED::::",
                "Shaking has stopped. I think we can all agree that Sensey rocks!"
            )
            Sensey.getInstance().stopShakeDetection(this)
            val intent = Intent(applicationContext, LocationService::class.java)
            val prefs = getSharedPreferences("fromFlutter", Context.MODE_PRIVATE)
            val numbers = prefs.getString("numbers", null)
            intent.putExtra("nums", numbers)
            startService(intent)
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
                Sensey.getInstance().stop()
                stopForeground(true)
                stopSelf()
                Log.e("Screen_ON", "Service and Sensey stopped when screen came on!")
            } else if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                startForeground(Process.myPid(), Notification())
                startService(Intent(applicationContext, TheService::class.java))
                Log.e("SCREEN_OFF ::", "RUNNABLE STARTS NEXT! SERVICE HAS STARTED. ")
                val runnable = Runnable {
                    Sensey.getInstance().startShakeDetection(20f, 1000, shakeListener)
//                    Sensey.getInstance()
//                     .startTouchTypeDetection(context, threeFingerSingleTapListener)
                }
                Handler().postDelayed(runnable, 500)
            }
        }
    }
}

