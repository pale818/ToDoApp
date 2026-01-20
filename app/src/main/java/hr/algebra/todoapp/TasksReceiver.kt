package hr.algebra.todoapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import hr.algebra.todoapp.framework.notificationsEnabled

class TasksReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Respect preference FIRST
        if (!context.notificationsEnabled()) return

        val taskId = intent.getLongExtra("TASK_ID", -1L)
        val title = intent.getStringExtra("TITLE") ?: "Task reminder"
        val text = intent.getStringExtra("TEXT") ?: "You have a task due."

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannel(nm)

        val openAppIntent = Intent(context, HostActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("TASK_ID", taskId) // optional: open edit/details
        }

        val contentPi = PendingIntent.getActivity(
            context,
            taskId.toInt().coerceAtLeast(0),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // change if you don't have it
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(contentPi)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notifId = if (taskId != -1L) taskId.toInt() else (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        nm.notify(notifId, notif)

        //BROADCAST
        if (taskId != 999L) return
        Log.d("TasksReceiver", "Alarm/trigger fired for taskId=$taskId")
        // Notify UI (your TasksFragment listens for this)
        context.sendBroadcast(Intent(ACTION_REMINDER_FIRED).apply {
            putExtra(EXTRA_TASK_ID, taskId)
        })



    }

    private fun ensureChannel(nm: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID,
                "Task reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(ch)
        }
    }

    companion object {
        private const val CHANNEL_ID = "task_reminders"

        //BROADCAST
        const val ACTION_REMINDER_FIRED = "hr.algebra.todoapp.ACTION_REMINDER_FIRED"
        const val EXTRA_TASK_ID = "TASK_ID"
    }


}
