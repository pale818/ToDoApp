package hr.algebra.todoapp.framework

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import hr.algebra.todoapp.TasksReceiver

object TaskReminderScheduler {

    fun schedule(context: Context, taskId: Long, title: String, dueAtMillis: Long) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, TasksReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
            putExtra("TITLE", title)
            putExtra("TEXT", "Task is due now.")
        }

        val pi = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (!alarm.canScheduleExactAlarms()) {
                    // Fallback: schedule inexact (still works), or just return
                    alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dueAtMillis, pi)
                    return
                }
            }
            alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dueAtMillis, pi)
        } catch (se: SecurityException) {
            // If exact alarms are not allowed, fallback
            alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dueAtMillis, pi)
        }
    }

    fun cancel(context: Context, taskId: Long) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, TasksReceiver::class.java)

        val pi = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarm.cancel(pi)
    }
}
