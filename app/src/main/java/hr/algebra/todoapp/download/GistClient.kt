package hr.algebra.todoapp.download

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GistClient {

    // TODO: paste YOUR baseUrl here (must end with '/')
    private const val BASE_URL =
        "https://gist.githubusercontent.com/pale818/caab10f29fbae5ea670dfb70da482e7e/raw/a0268a0dedb1b9fbcb53e7d606e86bb42fc2c14a/"

    val api: GistApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GistApi::class.java)
    }
}
