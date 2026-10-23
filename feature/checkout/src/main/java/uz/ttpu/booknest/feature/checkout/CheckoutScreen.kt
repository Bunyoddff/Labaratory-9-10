package uz.ttpu.booknest.feature.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.ttpu.booknest.core.ui.BookCard

@Composable
fun CheckoutScreen(
    state: CheckoutState,
    onIntent: (CheckoutIntent) -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val book = state.book
        when {
            state.isLoading -> CircularProgressIndicator()
            state.orderNumber != null -> {
                Text("Thank you! Your order number is ${state.orderNumber}")
                Button(onClick = onFinished) { Text("Done") }
            }
            book != null -> {
                BookCard(book = book)
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onIntent(CheckoutIntent.EmailChanged(it)) },
                    label = { Text("E-mail") },
                    isError = state.errorMessage != null,
                    singleLine = true,
                    enabled = !state.isPlacingOrder,
                )
                state.errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
                Button(
                    onClick = { onIntent(CheckoutIntent.PlaceOrderClicked) },
                    enabled = state.canSubmit,
                ) {
                    Text(if (state.isPlacingOrder) "Placing order…" else "Place order")
                }
            }
            else -> Text(state.errorMessage ?: "Book not found")
        }
    }
}
