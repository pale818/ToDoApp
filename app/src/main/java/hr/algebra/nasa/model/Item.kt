package hr.algebra.nasa.model

data class Item(
    var _id: Long?,
    val title: String,
    val explanation: String,
    val picturePath: String,
    val date: String,
    var read: Boolean
)