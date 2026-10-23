package uz.ttpu.booknest.feature.checkout

fun reduce(state: CheckoutState, intent: CheckoutIntent): CheckoutState = when (intent) {
    is CheckoutIntent.BookLoaded -> state.copy(
        isLoading = false,
        book = intent.book,
        errorMessage = if (intent.book == null) "Book not found" else null,
    )
    is CheckoutIntent.EmailChanged -> state.copy(
        email = intent.email,
        errorMessage = null,
    )
    is CheckoutIntent.PlaceOrderClicked -> {
        if (state.canSubmit) {
            state.copy(
                isPlacingOrder = true,
                errorMessage = null,
            )
        } else {
            state
        }
    }
    is CheckoutIntent.OrderPlaced -> state.copy(
        isPlacingOrder = false,
        orderNumber = intent.orderNumber,
    )
    is CheckoutIntent.OrderFailed -> state.copy(
        isPlacingOrder = false,
        errorMessage = intent.message,
    )
    is CheckoutIntent.ErrorDismissed -> state.copy(
        errorMessage = null,
    )
}
