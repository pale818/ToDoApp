package hr.algebra.todoapp.download

data class GistTaskDto(
    val title: String,
    val notes: String? = "",
    val priority: Int? = 0,
    val category: String? = "personal",
    val completed: Boolean? = false
)
