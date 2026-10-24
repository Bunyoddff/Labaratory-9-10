package uz.ttpu.booknest.data.books.impl

import uz.ttpu.booknest.data.books.api.BookRepository

object BooksDataModule {
    fun provideRepository(): BookRepository =
        InMemoryBookRepository(BookLocalDataSource())
}
