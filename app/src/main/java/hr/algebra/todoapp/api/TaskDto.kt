package hr.algebra.todoapp.api

import com.google.gson.annotations.SerializedName

data class TaskDto(
    @SerializedName("date") val date: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("title") val title: String
)

