package uz.ttpu.booknest.feature.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import uz.ttpu.booknest.data.books.api.BookRepository

@Composable
fun CatalogRoute(
    repository: BookRepository,
    onBookClick: (String) -> Unit,
) {
    val viewModel: CatalogViewModel = viewModel(
        factory = viewModelFactory {
            initializer { CatalogViewModel(repository) }
        },
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CatalogScreen(
        state = state,
        onBookClick = onBookClick,
        onFavoriteClick = viewModel::onFavoriteClick,
    )
}
