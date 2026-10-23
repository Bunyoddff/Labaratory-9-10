package uz.ttpu.booknest.feature.checkout

import uz.ttpu.booknest.core.model.Book

/** Everything that can happen on this screen: user actions and results. */
sealed interface CheckoutIntent {
    data class BookLoaded(val book: Book?) : CheckoutIntent
    data class EmailChanged(val email: String) : CheckoutIntent
    data object PlaceOrderClicked : CheckoutIntent
    data class OrderPlaced(val orderNumber: String) : CheckoutIntent
    data class OrderFailed(val message: String) : CheckoutIntent
    data object ErrorDismissed : CheckoutIntent
}
