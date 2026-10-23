package uz.ttpu.booknest

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uz.ttpu.booknest.feature.catalog.CatalogRoute
import uz.ttpu.booknest.feature.checkout.CheckoutRoute
import uz.ttpu.booknest.feature.favorites.FavoritesRoute

@Composable
fun BookNestNavHost(container: AppContainer) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val openBook: (String) -> Unit = { navController.navigate(Routes.checkout(it)) }

    Scaffold(
        bottomBar = {
            if (currentRoute == Routes.CATALOG || currentRoute == Routes.FAVORITES) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Routes.CATALOG,
                        onClick = { navController.navigateTopLevel(Routes.CATALOG) },
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { Text("Catalog") },
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.FAVORITES,
                        onClick = { navController.navigateTopLevel(Routes.FAVORITES) },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text("Favorites") },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.CATALOG,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.CATALOG) {
                CatalogRoute(
                    repository = container.bookRepository,
                    onBookClick = openBook,
                )
            }
            composable(Routes.FAVORITES) {
                FavoritesRoute(
                    repository = container.bookRepository,
                    onBookClick = openBook,
                )
            }
            composable(
                route = Routes.CHECKOUT,
                arguments = listOf(navArgument("bookId") { type = NavType.StringType }),
            ) {
                CheckoutRoute(
                    bookRepository = container.bookRepository,
                    orderRepository = container.orderRepository,
                    onFinished = { navController.popBackStack() },
                )
            }
        }
    }
}

private fun NavController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(Routes.CATALOG)
        launchSingleTop = true
    }
}
