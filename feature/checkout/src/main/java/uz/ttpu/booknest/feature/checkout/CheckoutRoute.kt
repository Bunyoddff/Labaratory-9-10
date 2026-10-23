package uz.ttpu.booknest.feature.checkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import uz.ttpu.booknest.data.books.api.BookRepository
import uz.ttpu.booknest.data.orders.OrderRepository

@Composable
fun CheckoutRoute(
    bookRepository: BookRepository,
    orderRepository: OrderRepository,
    onFinished: () -> Unit,
) {
    val viewModel: CheckoutViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                CheckoutViewModel(
                    createSavedStateHandle(),
                    bookRepository,
                    orderRepository,
                )
            }
        },
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    CheckoutScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onFinished = onFinished,
    )
}
