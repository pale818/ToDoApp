package hr.algebra.todoapp.download

import retrofit2.http.GET

interface GistApi {
    @GET("tasks.json")
    suspend fun fetchTasks(): List<GistTaskDto>
}
