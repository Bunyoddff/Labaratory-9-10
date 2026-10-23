package uz.ttpu.booknest.feature.favorites

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import uz.ttpu.booknest.core.model.Book
import uz.ttpu.booknest.core.testing.FakeBookRepository

/** A View that only writes down what it was told. */
private class RecordingView : FavoritesContract.View {
    val events = mutableListOf<String>()

    override fun showLoading() {
        events += "loading"
    }

    override fun showFavorites(books: List<Book>) {
        events += "favorites:" + books.joinToString(",") { it.id }
    }

    override fun showEmpty() {
        events += "empty"
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesPresenterTest {

    @Test
    fun showsLoadingThenEmptyThenFavorites() = runTest(UnconfinedTestDispatcher()) {
        val repository = FakeBookRepository()
        val view = RecordingView()
        val presenter = FavoritesPresenter(repository, backgroundScope)

        presenter.attach(view)
        repository.setFavorite("t1", true)

        assertEquals(listOf("loading", "empty", "favorites:t1"), view.events)
        presenter.detach()
    }

    @Test
    fun noUpdatesAfterDetach() = runTest(UnconfinedTestDispatcher()) {
        val repository = FakeBookRepository()
        val view = RecordingView()
        val presenter = FavoritesPresenter(repository, backgroundScope)

        presenter.attach(view)
        presenter.detach()
        repository.setFavorite("t2", true)

        assertEquals(listOf("loading", "empty"), view.events)
    }

    @Test
    fun removeClickClearsTheFavorite() = runTest(UnconfinedTestDispatcher()) {
        val repository = FakeBookRepository()
        val view = RecordingView()
        val presenter = FavoritesPresenter(repository, backgroundScope)
        presenter.attach(view)
        repository.setFavorite("t3", true)

        presenter.onRemoveClicked("t3")

        assertEquals("empty", view.events.last())
        presenter.detach()
    }

    @Test
    fun secondViewAttachedReceivesInitialStatesAndFirstReceivesNothingNew() = runTest(UnconfinedTestDispatcher()) {
        val repository = FakeBookRepository()
        val view1 = RecordingView()
        val view2 = RecordingView()
        val presenter = FavoritesPresenter(repository, backgroundScope)

        presenter.attach(view1)
        presenter.detach()

        presenter.attach(view2)
        assertEquals(listOf("loading", "empty"), view2.events)
        assertEquals(listOf("loading", "empty"), view1.events)
        presenter.detach()
    }
}
