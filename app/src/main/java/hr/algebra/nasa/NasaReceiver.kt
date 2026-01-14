package hr.algebra.nasa

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import hr.algebra.nasa.framework.setBooleanPreference

class NasaReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // FG
        context.setBooleanPreference(DATA_IMPORTED)
        context.startActivity(Intent(context, HostActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}