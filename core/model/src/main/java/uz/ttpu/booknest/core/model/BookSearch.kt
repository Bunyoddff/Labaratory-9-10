package uz.ttpu.booknest.core.model

internal fun String.normalizedTitle(): String = trim().lowercase()

fun Book.matches(query: String): Boolean =
    title.normalizedTitle().contains(query.normalizedTitle())
