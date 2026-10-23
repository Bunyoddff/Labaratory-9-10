package uz.ttpu.booknest.feature.favorites

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import uz.ttpu.booknest.data.books.api.BookRepository

class FavoritesPresenter(
    private val repository: BookRepository,
    private val scope: CoroutineScope,
) : FavoritesContract.Presenter {

    private var view: FavoritesContract.View? = null
    private var job: Job? = null

    override fun attach(view: FavoritesContract.View) {
        this.view = view
        view.showLoading()
        job = scope.launch {
            combine(
                repository.observeBooks(),
                repository.observeFavoriteIds(),
            ) { books, favoriteIds ->
                books.filter { it.id in favoriteIds }
            }.collect { favorites ->
                if (favorites.isEmpty()) {
                    this@FavoritesPresenter.view?.showEmpty()
                } else {
                    this@FavoritesPresenter.view?.showFavorites(favorites)
                }
            }
        }
    }

    override fun detach() {
        job?.cancel()
        job = null
        view = null
    }

    override fun onRemoveClicked(bookId: String) {
        scope.launch { repository.setFavorite(bookId, false) }
    }
}
