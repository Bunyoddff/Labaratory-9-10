package uz.ttpu.booknest.core.testing

import uz.ttpu.booknest.data.orders.OrderRepository

class StubOrderRepository(
    private val result: Result<String> = Result.success("ORD-TEST"),
) : OrderRepository {

    /** Every call as (bookId, email). */
    val placed = mutableListOf<Pair<String, String>>()

    override suspend fun placeOrder(bookId: String, email: String): Result<String> {
        placed += bookId to email
        return result
    }
}
