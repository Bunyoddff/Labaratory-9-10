package uz.ttpu.booknest.feature.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import uz.ttpu.booknest.data.books.api.BookRepository

@Composable
fun FavoritesRoute(
    repository: BookRepository,
    onBookClick: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val presenter = remember(repository, scope) { FavoritesPresenter(repository, scope) }
    val view = remember { FavoritesViewState() }

    DisposableEffect(presenter, view) {
        presenter.attach(view)
        onDispose { presenter.detach() }
    }

    FavoritesScreen(
        view = view,
        onRemoveClick = presenter::onRemoveClicked,
        onBookClick = onBookClick,
    )
}
