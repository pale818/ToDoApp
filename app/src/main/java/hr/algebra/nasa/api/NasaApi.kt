package hr.algebra.nasa.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/*
const val API_URL = "https://api.nasa.gov/planetary/"

interface NasaApi {
    @GET("apod?api_key=DEMO_KEY")
    fun fetchItems(@Query("count") count: Int = 10)
    : Call<List<NasaItem>>
}
 */

const val API_URL = "https://api.jsonbin.io/v3/b/"

interface NasaApi {
    @GET("695a9fc0d0ea881f40541f8a")
    fun fetchItems() : Call<Record>
}