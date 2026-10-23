package uz.ttpu.booknest.data.orders

import kotlinx.coroutines.delay

internal class FakeOrderRepository : OrderRepository {
    private var nextNumber = 1000

    override suspend fun placeOrder(bookId: String, email: String): Result<String> {
        delay(600) // pretend this is a payment server
        return if ("@" in email) {
            Result.success("ORD-${nextNumber++}")
        } else {
            Result.failure(IllegalArgumentException("Invalid e-mail address"))
        }
    }
}
