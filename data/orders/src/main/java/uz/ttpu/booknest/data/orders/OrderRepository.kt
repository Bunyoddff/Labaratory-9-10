package uz.ttpu.booknest.data.orders

interface OrderRepository {
    /** Returns the order number, or a failure with a readable message. */
    suspend fun placeOrder(bookId: String, email: String): Result<String>
}
