package uz.ttpu.booknest.feature.checkout

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.ttpu.booknest.data.books.api.BookRepository
import uz.ttpu.booknest.data.orders.OrderRepository

class CheckoutViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val bookRepository: BookRepository,
    private val orderRepository: OrderRepository,
    private val log: (String) -> Unit = { Log.d("CheckoutMvi", it) },
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle.get<String>("bookId"))

    private val _state = MutableStateFlow(
        CheckoutState(email = savedStateHandle.get<String>("email") ?: ""),
    )
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            onIntent(CheckoutIntent.BookLoaded(bookRepository.getBook(bookId)))
        }
    }

    fun onIntent(intent: CheckoutIntent) {
        val previous = _state.value
        val next = reduce(previous, intent)
        log("intent = $intent")
        if (next != previous) log("state  = $next")
        _state.value = next

        if (intent is CheckoutIntent.EmailChanged) {
            savedStateHandle["email"] = intent.email
        }

        if (intent == CheckoutIntent.PlaceOrderClicked &&
            !previous.isPlacingOrder && next.isPlacingOrder
        ) {
            placeOrder(next.email)
        }
    }

    private fun placeOrder(email: String) {
        viewModelScope.launch {
            orderRepository.placeOrder(bookId, email)
                .onSuccess { onIntent(CheckoutIntent.OrderPlaced(it)) }
                .onFailure {
                    onIntent(CheckoutIntent.OrderFailed(it.message ?: "Order failed"))
                }
        }
    }
}
