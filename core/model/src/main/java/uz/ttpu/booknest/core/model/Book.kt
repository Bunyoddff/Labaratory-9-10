package uz.ttpu.booknest.core.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val priceCents: Int,
    val rating: Double = 4.0,
)
