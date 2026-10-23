package uz.ttpu.booknest.feature.favorites

import uz.ttpu.booknest.core.model.Book

interface FavoritesContract {

    /** Passive: it only shows what the Presenter tells it to show. */
    interface View {
        fun showLoading()
        fun showFavorites(books: List<Book>)
        fun showEmpty()
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun onRemoveClicked(bookId: String)
    }
}
