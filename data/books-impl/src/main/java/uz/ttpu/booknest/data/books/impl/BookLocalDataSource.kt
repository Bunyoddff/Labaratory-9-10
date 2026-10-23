package uz.ttpu.booknest.data.books.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import uz.ttpu.booknest.core.model.Book

internal class BookLocalDataSource {
    private val books = MutableStateFlow(initialBooks)
    private val favoriteIds = MutableStateFlow<Set<String>>(emptySet())

    val booksFlow: Flow<List<Book>> = books
    val favoriteIdsFlow: Flow<Set<String>> = favoriteIds

    fun findBook(id: String): Book? = books.value.firstOrNull { it.id == id }

    fun setFavorite(id: String, favorite: Boolean) {
        favoriteIds.update { if (favorite) it + id else it - id }
    }
}

internal val initialBooks = listOf(
    Book("b1", "Kotlin in Action", "Dmitry Jemerov", 3500),
    Book("b2", "Clean Architecture", "Robert C. Martin", 3200),
    Book("b3", "Head First Android", "Dawn Griffiths", 4100),
    Book("b4", "Effective Java", "Joshua Bloch", 3900),
    Book("b5", "Refactoring", "Martin Fowler", 4500),
    Book("b6", "The Pragmatic Programmer", "Andrew Hunt", 3700),
)
