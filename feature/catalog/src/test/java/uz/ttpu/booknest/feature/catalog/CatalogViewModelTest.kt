package uz.ttpu.booknest.feature.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import uz.ttpu.booknest.core.testing.FakeBookRepository
import uz.ttpu.booknest.core.testing.MainDispatcherRule
import uz.ttpu.booknest.core.testing.testBooks

class CatalogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadsAllBooksWithoutFavorites() {
        val viewModel = CatalogViewModel(FakeBookRepository())

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(testBooks.size, state.items.size)
        assertTrue(state.items.none { it.isFavorite })
    }

    @Test
    fun favoriteClickMarksOnlyThatBook() {
        val viewModel = CatalogViewModel(FakeBookRepository())

        viewModel.onFavoriteClick("t2", true)

        val favorites = viewModel.uiState.value.items.filter { it.isFavorite }
        assertEquals(listOf("t2"), favorites.map { it.book.id })
    }

    @Test
    fun secondClickRemovesTheFavorite() {
        val viewModel = CatalogViewModel(FakeBookRepository())

        viewModel.onFavoriteClick("t1", true)
        viewModel.onFavoriteClick("t1", false)

        assertTrue(viewModel.uiState.value.items.none { it.isFavorite })
    }

    @Test
    fun favoritingTwoBooksMarksBothInState() {
        val viewModel = CatalogViewModel(FakeBookRepository())

        viewModel.onFavoriteClick("t1", true)
        viewModel.onFavoriteClick("t2", true)

        val favorites = viewModel.uiState.value.items.filter { it.isFavorite }
        assertEquals(listOf("t1", "t2"), favorites.map { it.book.id }.sorted())
    }
}
