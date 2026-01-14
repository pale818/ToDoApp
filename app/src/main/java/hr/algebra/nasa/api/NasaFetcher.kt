package hr.algebra.nasa.api

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import hr.algebra.nasa.NASA_PROVIDER_CONTENT_URI
import hr.algebra.nasa.model.Item
import hr.algebra.nasa.NasaReceiver
import hr.algebra.nasa.handler.downloadAndStoreImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class NasaFetcher(private val context: Context) {

    private var nasaApi: NasaApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        nasaApi = retrofit.create<NasaApi>()
    }

    fun fetchItems(count: Int = 10) {
        val request = nasaApi.fetchItems()

        request.enqueue(object : Callback<Record> {
            override fun onResponse(
                call: Call<Record?>,
                response: Response<Record?>
            ) {
                response.body()?.record.let { populateItems(it) }
            }

            override fun onFailure(
                call: Call<Record?>,
                t: Throwable
            ) {
                Log.d("DOWNLOAD FAILURE", t.message, t)

            }
        })

    }

    private fun populateItems(nasaItems: List<NasaItem>?) {
        //val items = mutableListOf<Item>()
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            nasaItems?.forEach {
                val picturePath = downloadAndStoreImage(context, it.url)

                val values = ContentValues().apply {
                    put(Item::title.name, it.title)
                    put(Item::explanation.name, it.explanation)
                    put(Item::picturePath.name, picturePath ?: "")
                    put(Item::date.name, it.date)
                    put(Item::read.name, false)
                }

                context.contentResolver.insert(
                    NASA_PROVIDER_CONTENT_URI,
                    values
                )
            }

            context.sendBroadcast(Intent(context, NasaReceiver::class.java))
        }

    }

}