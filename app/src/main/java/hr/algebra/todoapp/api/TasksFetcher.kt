/*

package hr.algebra.todoapp.api

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import hr.algebra.todoapp.TODO_PROVIDER_CONTENT_URI
import hr.algebra.todoapp.model.Task
import hr.algebra.todoapp.TasksReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class TasksFetcher(private val context: Context) {

    private val tasksApi: TasksApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        tasksApi = retrofit.create(TasksApi::class.java)
    }

    fun fetchTasks(count: Int = 10) {
        val request = tasksApi.fetchTasks()

        request.enqueue(object : Callback<RecordDto> {
            override fun onResponse(
                call: Call<RecordDto?>,
                response: Response<RecordDto?>
            ) {
                response.body()?.record.let { populateTasks(it) }
            }

            override fun onFailure(
                call: Call<RecordDto?>,
                t: Throwable
            ) {
                Log.d("DOWNLOAD FAILURE", t.message, t)

            }
        })

    }

    private fun populateTasks(taskDtos: List<TaskDto>?) {
        //val items = mutableListOf<Item>()
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            taskDtos?.forEach {

                val values = ContentValues().apply {
                    put(Task::title.name, it.title)
                    put(Task::notes.name, it.explanation)
                    put(Task::dueDate.name, it.date)
                    put(Task::done.name, false)
                    put(Task::priority.name, 0)
                }

                context.contentResolver.insert(
                    TODO_PROVIDER_CONTENT_URI,
                    values
                )
            }

            context.sendBroadcast(Intent(context, TasksReceiver::class.java))
        }

    }

}*/
