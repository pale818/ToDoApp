package hr.algebra.nasa.handler

import android.content.Context
import android.util.Log
import hr.algebra.nasa.factory.createGetHttpUrlConnection
import java.io.File
import java.net.HttpURLConnection
import java.nio.file.Files
import java.nio.file.Paths


fun downloadAndStoreImage(context: Context, url: String) : String? {

    val filename = url.substring(url.lastIndexOf(File.separator)) + 1
    val file: File = createFile(context, filename)
    try {

        val con: HttpURLConnection = createGetHttpUrlConnection(url)
        Files.copy(
            con.getInputStream(),
            Paths.get(file.toURI()))

        return file.absolutePath

    } catch (e: Exception) {
        Log.e("IMAGE DOWNLOADER ERROR", e.message, e)
    }

    return null
}

fun createFile(context: Context, filename: String): File {
    val dir = context.applicationContext.getExternalFilesDir(null)
    val file = File(dir, filename)
    if(file.exists()) file.delete()

    return file
}
