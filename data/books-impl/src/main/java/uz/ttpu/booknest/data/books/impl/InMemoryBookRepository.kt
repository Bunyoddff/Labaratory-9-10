package uz.ttpu.booknest.data.books.impl

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import uz.ttpu.booknest.core.model.Book
import uz.ttpu.booknest.data.books.api.BookRepository

internal class InMemoryBookRepository(
    private val dataSource: BookLocalDataSource,
) : BookRepository {

    override fun observeBooks(): Flow<List<Book>> = dataSource.booksFlow

    override fun observeFavoriteIds(): Flow<Set<String>> = dataSource.favoriteIdsFlow

    override suspend fun getBook(id: String): Book? {
        delay(300) // pretend this is a network call
        return dataSource.findBook(id)
    }

    override suspend fun setFavorite(id: String, favorite: Boolean) {
        dataSource.setFavorite(id, favorite)
    }
}
