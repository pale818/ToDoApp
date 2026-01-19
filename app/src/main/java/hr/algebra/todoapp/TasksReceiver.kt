package hr.algebra.todoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hr.algebra.todoapp.framework.setBooleanPreference
import hr.algebra.todoapp.framework.notificationsEnabled

class TasksReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // FG
        context.setBooleanPreference(DATA_IMPORTED)
        context.startActivity(Intent(context, HostActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })

        //NOTIFICATIONS
        if (!context.notificationsEnabled()) return
    }





}