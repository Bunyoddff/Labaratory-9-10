package uz.ttpu.booknest

import uz.ttpu.booknest.data.books.api.BookRepository
import uz.ttpu.booknest.data.orders.OrderRepository
import uz.ttpu.booknest.data.orders.OrdersDataModule
import uz.ttpu.booknest.di.provideBookRepository

/** The composition root: the only place that knows the implementations. */
class AppContainer {
    val bookRepository: BookRepository = provideBookRepository()
    val orderRepository: OrderRepository = OrdersDataModule.provideRepository()
}
