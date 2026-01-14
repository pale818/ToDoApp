package hr.algebra.nasa.api

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NasaWorker(private val context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {
    override fun doWork(): Result {
        // BG
        NasaFetcher(context).fetchItems()
        return Result.success()
    }
}