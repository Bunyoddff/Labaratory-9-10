package uz.ttpu.booknest.di

import uz.ttpu.booknest.data.books.api.BookRepository
import uz.ttpu.booknest.data.books.impl.BooksDataModule

fun provideBookRepository(): BookRepository = BooksDataModule.provideRepository()
