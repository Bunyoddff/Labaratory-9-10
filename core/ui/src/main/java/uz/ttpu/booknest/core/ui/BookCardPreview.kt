package uz.ttpu.booknest.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import uz.ttpu.booknest.core.model.Book
import uz.ttpu.booknest.core.ui.theme.BookNestTheme

@Preview(showBackground = true)
@Composable
private fun BookCardPreview() {
    BookNestTheme {
        BookCard(book = Book("p1", "Kotlin in Action", "Dmitry Jemerov", 3500))
    }
}
