package uz.ttpu.booknest.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.ttpu.booknest.core.model.Book
import uz.ttpu.booknest.core.util.displayPrice

@Composable
fun BookCard(
    book: Book,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val clickModifier = onClick?.let { Modifier.clickable(onClick = it) } ?: Modifier
    Card(modifier = modifier.fillMaxWidth().then(clickModifier)) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium)
                Text(book.author, style = MaterialTheme.typography.bodyMedium)
                Text(book.displayPrice(), style = MaterialTheme.typography.labelLarge)
                Text("Rating: ${book.rating}", style = MaterialTheme.typography.bodySmall)
            }
            trailing?.invoke()
        }
    }
}
