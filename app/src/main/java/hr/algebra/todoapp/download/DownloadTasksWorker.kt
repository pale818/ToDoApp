package hr.algebra.todoapp.download

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import hr.algebra.todoapp.TODO_PROVIDER_CONTENT_URI
import hr.algebra.todoapp.model.Task

class DownloadTasksWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("DownloadTasksWorker", "Downloading JSON from Gist...")

            val remote = GistClient.api.fetchTasks()

            // Insert into your existing DB via ContentResolver
            remote.forEach { dto ->
                val values = ContentValues().apply {
                    put(Task::title.name, dto.title)
                    put(Task::notes.name, dto.notes ?: "")
                    put(Task::priority.name, dto.priority ?: 0)
                    put(Task::category.name, dto.category ?: "personal")
                    put(Task::done.name, if (dto.completed == true) 1 else 0)
                    // dueDate optional; leave null
                }
                applicationContext.contentResolver.insert(TODO_PROVIDER_CONTENT_URI, values)
            }

            // Broadcast so UI can refresh
            applicationContext.sendBroadcast(Intent(ACTION_DOWNLOAD_FINISHED))

            Log.d("DownloadTasksWorker", "Download + insert finished.")
            Result.success()

        } catch (e: Exception) {
            Log.e("DownloadTasksWorker", "Download failed", e)
            Result.retry()
        }
    }

    companion object {
        const val ACTION_DOWNLOAD_FINISHED = "hr.algebra.todoapp.ACTION_DOWNLOAD_FINISHED"
    }
}
