package uz.ttpu.booknest.data.books.demo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import uz.ttpu.booknest.core.model.Book
import uz.ttpu.booknest.data.books.api.BookRepository

object DemoBooksDataModule {
    fun provideRepository(): BookRepository = DemoBookRepository()
}

internal class DemoBookRepository : BookRepository {
    private val books = MutableStateFlow(
        listOf(
            Book("d1", "Demo Book A", "Demo Author", 100),
            Book("d2", "Demo Book B", "Demo Author", 200),
            Book("d3", "Demo Book C", "Demo Author", 300),
        ),
    )
    private val favoriteIds = MutableStateFlow<Set<String>>(emptySet())

    override fun observeBooks(): Flow<List<Book>> = books

    override fun observeFavoriteIds(): Flow<Set<String>> = favoriteIds

    override suspend fun getBook(id: String): Book? =
        books.value.firstOrNull { it.id == id }

    override suspend fun setFavorite(id: String, favorite: Boolean) {
        favoriteIds.update { if (favorite) it + id else it - id }
    }
}
