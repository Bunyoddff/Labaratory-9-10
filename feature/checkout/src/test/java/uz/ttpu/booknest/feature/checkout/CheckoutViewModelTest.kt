package uz.ttpu.booknest.feature.checkout

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import uz.ttpu.booknest.core.testing.FakeBookRepository
import uz.ttpu.booknest.core.testing.MainDispatcherRule
import uz.ttpu.booknest.core.testing.StubOrderRepository

class CheckoutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(
        orders: StubOrderRepository = StubOrderRepository(),
        log: (String) -> Unit = {},
    ) = CheckoutViewModel(
        savedStateHandle = SavedStateHandle(mapOf("bookId" to "t1")),
        bookRepository = FakeBookRepository(),
        orderRepository = orders,
        log = log,
    )

    @Test
    fun loadsTheBookFromTheNavigationArgument() {
        val state = viewModel().state.value

        assertFalse(state.isLoading)
        assertEquals("t1", state.book?.id)
    }

    @Test
    fun successfulOrderEndsWithTheOrderNumber() {
        val orders = StubOrderRepository(Result.success("ORD-1"))
        val viewModel = viewModel(orders)

        viewModel.onIntent(CheckoutIntent.EmailChanged("a@b.uz"))
        viewModel.onIntent(CheckoutIntent.PlaceOrderClicked)

        assertEquals("ORD-1", viewModel.state.value.orderNumber)
        assertEquals(listOf("t1" to "a@b.uz"), orders.placed)
    }

    @Test
    fun failedOrderShowsTheMessageAndAllowsAnotherTry() {
        val orders = StubOrderRepository(
            Result.failure(IllegalStateException("Card declined")),
        )
        val viewModel = viewModel(orders)

        viewModel.onIntent(CheckoutIntent.EmailChanged("a@b.uz"))
        viewModel.onIntent(CheckoutIntent.PlaceOrderClicked)

        val state = viewModel.state.value
        assertEquals("Card declined", state.errorMessage)
        assertNull(state.orderNumber)
        assertEquals(true, state.canSubmit)
    }

    @Test
    fun logsEveryIntentAndEveryStateChange() {
        val lines = mutableListOf<String>()
        val viewModel = viewModel(log = { lines += it })

        viewModel.onIntent(CheckoutIntent.EmailChanged("a@b.uz"))

        assertTrue(lines.any { it.startsWith("intent = EmailChanged") })
        assertTrue(lines.any { it.startsWith("state") })
    }

    @Test
    fun restoresTheEmailAfterProcessDeath() {
        val handle = SavedStateHandle(mapOf("bookId" to "t1", "email" to "a@b.uz"))
        val viewModel = CheckoutViewModel(
            savedStateHandle = handle,
            bookRepository = FakeBookRepository(),
            orderRepository = StubOrderRepository(),
            log = {},
        )

        assertEquals("a@b.uz", viewModel.state.value.email)
    }

    @Test
    fun typedEmailIsWrittenToTheSavedState() {
        val handle = SavedStateHandle(mapOf("bookId" to "t1"))
        val viewModel = CheckoutViewModel(
            savedStateHandle = handle,
            bookRepository = FakeBookRepository(),
            orderRepository = StubOrderRepository(),
            log = {},
        )

        viewModel.onIntent(CheckoutIntent.EmailChanged("x@y.uz"))

        assertEquals("x@y.uz", handle.get<String>("email"))
    }
}
