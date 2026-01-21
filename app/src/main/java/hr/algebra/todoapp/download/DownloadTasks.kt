package hr.algebra.todoapp.download

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

object DownloadTasks {
    fun enqueue(context: Context): java.util.UUID {
        val req = androidx.work.OneTimeWorkRequestBuilder<DownloadTasksWorker>().build()
        androidx.work.WorkManager.getInstance(context).enqueue(req)
        return req.id
    }
}

