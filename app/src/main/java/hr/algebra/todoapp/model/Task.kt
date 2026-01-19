package hr.algebra.todoapp.model
data class Task(
    val _id: Long? = null,
    val title: String,
    val notes: String,
    val done: Boolean,
    val priority: Int,
    val dueDate: Long? = null,
    val category: String = "personal"

)
