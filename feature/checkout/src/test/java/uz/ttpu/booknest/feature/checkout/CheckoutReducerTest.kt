package uz.ttpu.booknest.feature.checkout

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.ttpu.booknest.core.model.Book

class CheckoutReducerTest {

    private val book = Book("b1", "Title", "Author", 1000)
    private val loaded = CheckoutState(isLoading = false, book = book)

    @Test
    fun bookLoadedStopsLoading() {
        val state = reduce(CheckoutState(), CheckoutIntent.BookLoaded(book))

        assertFalse(state.isLoading)
        assertEquals(book, state.book)
    }

    @Test
    fun missingBookShowsAnError() {
        val state = reduce(CheckoutState(), CheckoutIntent.BookLoaded(null))

        assertEquals("Book not found", state.errorMessage)
    }

    @Test
    fun cannotSubmitWithoutEmail() {
        assertFalse(loaded.canSubmit)
    }

    @Test
    fun emailEnablesSubmit() {
        val state = reduce(loaded, CheckoutIntent.EmailChanged("a@b.uz"))

        assertTrue(state.canSubmit)
    }

    @Test
    fun placeOrderIsIgnoredWhenSubmitIsNotAllowed() {
        assertEquals(loaded, reduce(loaded, CheckoutIntent.PlaceOrderClicked))
    }

    @Test
    fun placeOrderStartsTheSpinner() {
        val ready = loaded.copy(email = "a@b.uz")

        val state = reduce(ready, CheckoutIntent.PlaceOrderClicked)

        assertTrue(state.isPlacingOrder)
        assertFalse(state.canSubmit)
    }

    @Test
    fun failureKeepsTheEmailAndShowsTheMessage() {
        val busy = loaded.copy(email = "a@b.uz", isPlacingOrder = true)

        val state = reduce(busy, CheckoutIntent.OrderFailed("No network"))

        assertFalse(state.isPlacingOrder)
        assertEquals("a@b.uz", state.email)
        assertEquals("No network", state.errorMessage)
    }

    @Test
    fun reducerDoesNotChangeItsInput() {
        val before = loaded.copy(email = "a@b.uz")
        val snapshot = before.copy()

        reduce(before, CheckoutIntent.PlaceOrderClicked)

        assertEquals(snapshot, before)
    }

    @Test
    fun placeOrderClickedAfterOrderPlacedLeavesStateUnchanged() {
        val placed = loaded.copy(email = "a@b.uz", orderNumber = "ORD-1234")
        val next = reduce(placed, CheckoutIntent.PlaceOrderClicked)
        assertEquals(placed, next)
    }
}
