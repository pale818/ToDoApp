package hr.algebra.nasa.framework

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import androidx.preference.PreferenceManager
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.edit
import androidx.core.content.getSystemService

fun View.applyAnimation(id: Int) =
    startAnimation(AnimationUtils.loadAnimation(context, id))

fun Context.setBooleanPreference(key: String, value: Boolean = true) {
    PreferenceManager.getDefaultSharedPreferences(this)
        .edit {
            putBoolean(key, value)
        }
}

fun Context.getBooleanPreference(key: String) : Boolean {
    return PreferenceManager.getDefaultSharedPreferences(this)
        .getBoolean(key, false)
}
fun Context.amOnline() : Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>()
    connectivityManager?.activeNetwork?.let { network ->
        connectivityManager.getNetworkCapabilities(network)?.let { capabilities ->
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }

    }
    return false
}

fun callDelayed(delay: Long, work: Runnable) {
    Handler(Looper.getMainLooper()).postDelayed(
        work,
        delay
    )
}
