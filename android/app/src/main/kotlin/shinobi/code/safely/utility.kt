package shinobi.code.safely

import android.telephony.SmsManager
import android.util.Log
import java.lang.Exception

fun sendSms(number: String?, message: String?): Int{
    Log.e("Inside sendSms", "Okay we're now inside the send sms method, yaaaay")
    return try {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(
            number,
            null,
            message ,
            null,
            null
        )
        Log.e("SendSms:", "Sent sms")
        1
    } catch (ex: Exception) {
        ex.printStackTrace()
        0
    }
}