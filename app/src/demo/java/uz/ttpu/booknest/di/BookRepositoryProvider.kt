package uz.ttpu.booknest.di

import uz.ttpu.booknest.data.books.api.BookRepository
import uz.ttpu.booknest.data.books.demo.DemoBooksDataModule

fun provideBookRepository(): BookRepository = DemoBooksDataModule.provideRepository()
