package hr.algebra.todoapp.api

import com.google.gson.annotations.SerializedName

data class RecordDto(
    @SerializedName("record") val record : List<TaskDto>
)
