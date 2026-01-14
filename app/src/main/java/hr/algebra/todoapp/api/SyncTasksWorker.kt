package hr.algebra.todoapp.api

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class SyncTasksWorker(private val context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {
    override fun doWork(): Result {
        // BG
        TasksFetcher(context).fetchTasks()
        return Result.success()
    }
}