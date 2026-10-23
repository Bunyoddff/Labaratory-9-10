package uz.ttpu.booknest

object Routes {
    const val CATALOG = "catalog"
    const val FAVORITES = "favorites"
    const val CHECKOUT = "checkout/{bookId}"

    fun checkout(bookId: String) = "checkout/$bookId"
}
