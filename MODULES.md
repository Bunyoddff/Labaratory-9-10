# BookNest — Modular Architecture & Architectural Design Patterns

This document describes the modular architecture, dependency rules, architectural design patterns, and justification for the **BookNest** project (Laboratory 9 & 10).

---

## 1. Target Module Map

```
BookNest
├── app                       Entry point, navigation, DI container, flavors (full, demo)
├── core
│   ├── model                 Domain models (Book) with zero dependencies
│   ├── util                  Formatting utilities (PriceFormatter, BookPrice)
│   ├── ui                    Shared Compose theme (Color, Theme, Type) and BookCard
│   └── testing               Shared test doubles (FakeBookRepository, StubOrderRepository, MainDispatcherRule)
├── data
│   ├── books-api             Book data abstraction (BookRepository interface)
│   ├── books-impl            In-memory book data source implementation
│   ├── books-demo            Demo book repository with fixed sample books
│   └── orders                Orders domain (OrderRepository interface + FakeOrderRepository)
└── feature
    ├── catalog               Book catalog browsing & favoriting (MVVM)
    ├── favorites             Favorites list and management (MVP)
    └── checkout              Order placement with email entry (MVI)
```

---

## 2. Module Purposes

| Module | Type | Purpose |
| :--- | :--- | :--- |
| `:app` | Android Application | Composition root, top-level navigation (`BookNestNavHost`), dependency injection (`AppContainer`), and product flavors (`full` and `demo`). |
| `:core:model` | Kotlin Library | Core domain models (`Book`). Depends on nothing, maximum stability. |
| `:core:util` | Kotlin Library | Reusable business and presentation utilities (`PriceFormatter`, `BookPrice`). Pure JVM, fast unit tests. |
| `:core:ui` | Android Library | Shared Jetpack Compose UI components (`BookCard`) and Material 3 design system theme (`BookNestTheme`). |
| `:core:testing` | Kotlin Library | Test doubles, test data (`testBooks`), and JUnit rules (`MainDispatcherRule`) for feature and unit tests. |
| `:data:books-api` | Kotlin Library | Public abstraction for book operations (`BookRepository` interface). Enables dependency inversion. |
| `:data:books-impl` | Kotlin Library | Production in-memory implementation of `BookRepository` (`InMemoryBookRepository`, `BookLocalDataSource`). Hidden from features. |
| `:data:books-demo` | Kotlin Library | Demo flavor implementation of `BookRepository` (`DemoBookRepository`) providing sample preview books. |
| `:data:orders` | Kotlin Library | Order domain contract and mock payment server implementation (`OrderRepository`, `FakeOrderRepository`). |
| `:feature:catalog` | Android Library | Catalog screen with list of books and favorite toggling. Uses **MVVM** pattern. |
| `:feature:favorites` | Android Library | Favorites screen with list of saved books and removal action. Uses **MVP** pattern. |
| `:feature:checkout` | Android Library | Book checkout and ordering screen with validation and state preservation. Uses **MVI** pattern. |

---

## 3. Dependency Rules of this Project

1. **Features depend on API abstractions only (`-api`), never on implementations (`-impl`):**
   - Features (`:feature:catalog`, `:feature:favorites`, `:feature:checkout`) depend on `:data:books-api`. They never know how book data is retrieved or stored.
2. **Only `:app` may depend on implementation modules (`-impl`, `-demo`):**
   - The application module is the composition root. It wires implementations to abstractions using flavor-specific providers (`provideBookRepository()`).
3. **No module may depend on `:app`:**
   - `:app` is the leaf consumer at the top of the graph. Depending on `:app` creates circular dependencies and tight coupling.
4. **Features are independent (no feature-to-feature dependencies):**
   - Feature modules never depend on other feature modules. Navigation between features is mediated by `:app` through callbacks (e.g., `onBookClick: (String) -> Unit`) and IDs (`bookId: String`).
5. **Production modules never depend on `:core:testing`:**
   - `:core:testing` is only linked via `testImplementation(project(":core:testing"))`. Test doubles and test rules are never packaged into production APKs.

---

## 4. Architectural Patterns in Feature Modules

### Catalog: Model-View-ViewModel (MVVM)
- **Pattern:** MVVM (`CatalogViewModel`, `CatalogUiState`, `CatalogScreen`, `CatalogRoute`).
- **Reason:** The Catalog screen displays a continuous stream of data combined from multiple reactive flows (`observeBooks()` and `observeFavoriteIds()`). `CatalogViewModel` exposes an observable `StateFlow<CatalogUiState>`. Compose observes this state seamlessly, and UI events (`onFavoriteClick`) are forwarded to the ViewModel without tight coupling. The ViewModel automatically survives configuration changes (screen rotations).

### Favorites: Model-View-Presenter (MVP)
- **Pattern:** MVP (`FavoritesContract.View`, `FavoritesContract.Presenter`, `FavoritesViewState`, `FavoritesScreen`).
- **Reason:** Demonstrates explicit separation of concerns with a passive view. In MVP, the View implements `FavoritesContract.View` and is completely passive—it does not query models or repositories directly. The `FavoritesPresenter` mediates all interaction with `BookRepository`, filters favorite items, and commands the view (`showLoading()`, `showEmpty()`, `showFavorites()`). The Presenter contains pure Kotlin logic with no Android framework dependencies, making it directly testable using a `RecordingView`.

### Checkout: Model-View-Intent (MVI)
- **Pattern:** MVI (`CheckoutState`, `CheckoutIntent`, `CheckoutReducer`, `CheckoutViewModel`, `CheckoutScreen`).
- **Reason:** The Checkout flow involves multiple discrete asynchronous transitions (loading book details, user typing email, validating form submission, placing order with payment simulation, handling success or failure). MVI models the entire screen state as a single immutable `CheckoutState`. Every event or side effect is an explicit `CheckoutIntent`. The pure `reduce()` function ensures deterministic, reproducible state transitions that are trivial to test and log. Process death survival is achieved by restoring and persisting `email` in `SavedStateHandle`.

---

## 5. Justification for `api()` Dependencies

In this project, `implementation` is used by default to prevent dependency leaking and keep compile classpaths minimal. The few occurrences of `api(...)` are explicitly justified:

1. **`:data:books-api` -> `api(project(":core:model"))`:**
   - *Justification:* `BookRepository` returns `Flow<List<Book>>` and takes/returns `Book`. Any consumer of `BookRepository` inherently needs access to `Book`. Declaring `:core:model` as `api` saves consumers from having to declare redundant transitive dependencies.
2. **`:data:books-api` -> `api(libs.kotlinx.coroutines.core)`:**
   - *Justification:* `BookRepository` declares `Flow` return types and `suspend` functions in its public signatures. Consumers must have coroutines types visible on their compile classpath.
3. **`:core:testing` -> `api(...)` (all 5 dependencies):**
   - *Justification:* `:core:testing` is a shared test harness module. When a feature declares `testImplementation(project(":core:testing"))`, it immediately inherits `Book`, `BookRepository`, `OrderRepository`, `junit`, and `kotlinx-coroutines-test` without re-listing them in every feature's `build.gradle.kts`.

---

## 6. Design Proposal: "Order History" Screen

If tasked with adding an **Order History** screen:
- **Module:** Create a new feature module `:feature:orderhistory` (Android Library with Compose).
- **Pattern:** **MVVM** (Model-View-ViewModel).
  - *Rationale:* Order history is fundamentally a read-mostly list screen with pull-to-refresh or filter criteria, perfectly suited for reactive data observation via `StateFlow` and Compose `LazyColumn`.
- **Dependencies:**
  - `:core:model` (for domain models like `Order` / `Book`).
  - `:core:ui` (for theme, standard list cards, and loading indicators).
  - `:data:orders` (or a dedicated `:data:orders-api` if orders are split into API and implementation) to observe historical orders (`observeOrders(): Flow<List<Order>>`).
  - `:app` will declare `implementation(project(":feature:orderhistory"))` and add a new route `Routes.ORDER_HISTORY` to `BookNestNavHost`. No feature-to-feature dependencies are introduced.
