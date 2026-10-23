package uz.ttpu.booknest.feature.checkout

import uz.ttpu.booknest.core.model.Book

data class CheckoutState(
    val isLoading: Boolean = true,
    val book: Book? = null,
    val email: String = "",
    val isPlacingOrder: Boolean = false,
    val orderNumber: String? = null,
    val errorMessage: String? = null,
) {
    val canSubmit: Boolean
        get() = book != null &&
            email.isNotBlank() &&
            !isPlacingOrder &&
            orderNumber == null
}
