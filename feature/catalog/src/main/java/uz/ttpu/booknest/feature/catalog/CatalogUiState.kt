package uz.ttpu.booknest.feature.catalog

import uz.ttpu.booknest.core.model.Book

data class CatalogItem(
    val book: Book,
    val isFavorite: Boolean,
)

data class CatalogUiState(
    val isLoading: Boolean = true,
    val items: List<CatalogItem> = emptyList(),
)
