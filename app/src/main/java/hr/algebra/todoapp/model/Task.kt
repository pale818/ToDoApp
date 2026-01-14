package hr.algebra.todoapp.model

data class Task(
    var _id: Long?,
    val title: String,
    val notes: String,
    val dueDate: String,
    var done: Boolean,
    var priority: Int
)