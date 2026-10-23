package uz.ttpu.booknest.feature.catalog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import uz.ttpu.booknest.core.model.Book
import uz.ttpu.booknest.core.ui.theme.BookNestTheme

private val previewItems = listOf(
    CatalogItem(Book("p1", "Kotlin in Action", "J. Jemerov", 3500), isFavorite = true),
    CatalogItem(Book("p2", "Clean Architecture", "R. Martin", 3200), isFavorite = false),
)

@Preview(showBackground = true)
@Composable
private fun CatalogLoadedPreview() {
    BookNestTheme {
        CatalogScreen(
            state = CatalogUiState(isLoading = false, items = previewItems),
            onBookClick = {},
            onFavoriteClick = { _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogLoadingPreview() {
    BookNestTheme {
        CatalogScreen(
            state = CatalogUiState(isLoading = true),
            onBookClick = {},
            onFavoriteClick = { _, _ -> },
        )
    }
}
