package uz.ttpu.booknest.feature.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import uz.ttpu.booknest.data.books.api.BookRepository

class CatalogViewModel(
    private val repository: BookRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.observeBooks(),
                repository.observeFavoriteIds(),
            ) { books, favoriteIds ->
                books.map { CatalogItem(it, isFavorite = it.id in favoriteIds) }
            }.collect { items ->
                _uiState.value = CatalogUiState(isLoading = false, items = items)
            }
        }
    }

    fun onFavoriteClick(bookId: String, favorite: Boolean) {
        viewModelScope.launch { repository.setFavorite(bookId, favorite) }
    }
}
