# BookNest — Modularization & Architectural Design Patterns

**BookNest** is a multi-module Android application built with **Kotlin**, **Jetpack Compose (Material 3)**, and **Gradle Kotlin DSL**.

This project implements the requirements of **Laboratory 9 & 10 (Software Mobile Development)**, transforming a monolithic single-module baseline into a cleanly decoupled multi-module architecture and applying different architectural design patterns across features (**MVVM**, **MVP**, and **MVI**).

---

## 1. Project Overview & Key Accomplishments

- **Multi-Module Restructuring:** Refactored a single `:app` module into 11 distinct modules across `core`, `data`, and `feature` layers following high cohesion and low coupling principles.
- **Architectural Patterns Applied:**
  - **Catalog Screen (`:feature:catalog`):** Implemented using **MVVM (Model-View-ViewModel)** with reactive `StateFlow` streams.
  - **Favorites Screen (`:feature:favorites`):** Transitioned from **MVC** to **MVP (Model-View-Presenter)** with a passive view and clear contract interface.
  - **Checkout Screen (`:feature:checkout`):** Built using **MVI (Model-View-Intent)** with Unidirectional Data Flow (UDF), pure functional state reducers, and `SavedStateHandle` for process death survival.
- **Dependency Inversion & Build Flavors:**
  - Decoupled data contracts (`:data:books-api`) from data implementations.
  - Configured `full` and `demo` product flavors in `:app`, swapping in-memory data (`:data:books-impl`) and demo data (`:data:books-demo`) at compile/wiring time without altering feature code.
- **Comprehensive JVM Unit Testing:**
  - Written unit tests for ViewModels, Presenters, Reducers, and Utility modules running on the JVM via test doubles (`FakeBookRepository`, `StubOrderRepository`) and `MainDispatcherRule`.

---

## 2. Module Architecture

```
BookNest
├── app                     # Entry point, navigation, DI container, flavors (full, demo)
├── core
│   ├── model               # Pure Kotlin domain models (Book) — 0 dependencies
│   ├── util                # Formatting utilities (PriceFormatter, BookPrice)
│   ├── ui                  # Shared Material 3 Compose theme & BookCard component
│   └── testing             # Shared test doubles (Fakes, Stubs, Dispatcher rules)
├── data
│   ├── books-api           # BookRepository interface abstraction (public API)
│   ├── books-impl          # Production in-memory BookRepository implementation
│   ├── books-demo          # Demo BookRepository implementation with mock items
│   └── orders              # OrderRepository contract and payment server simulation
└── feature
    ├── catalog             # Catalog browsing & favoriting (MVVM)
    ├── favorites           # Favorites management (MVP)
    └── checkout            # Book ordering and validation (MVI)
```

### Module Responsibilities

| Module | Type | Purpose |
| :--- | :--- | :--- |
| `:app` | Android App | Composition root, top-level navigation (`BookNestNavHost`), dependency injection (`AppContainer`), and flavor management. |
| `:core:model` | Kotlin Library | Core domain models (`Book`). Maximum stability with zero dependencies. |
| `:core:util` | Kotlin Library | Business/display utilities (`PriceFormatter`). Pure JVM with fast unit tests. |
| `:core:ui` | Android Library | Reusable Jetpack Compose UI components (`BookCard`) and Material 3 theme. |
| `:core:testing` | Kotlin Library | Shared test harness, test doubles (`FakeBookRepository`, `StubOrderRepository`), and JUnit rules. |
| `:data:books-api` | Kotlin Library | Public abstraction for book operations (`BookRepository` interface). |
| `:data:books-impl` | Kotlin Library | Internal in-memory data source and repository implementation. |
| `:data:books-demo` | Kotlin Library | Fixed demo catalog implementation for the `demo` flavor. |
| `:data:orders` | Kotlin Library | Orders domain interface (`OrderRepository`) and simulation fake. |
| `:feature:catalog` | Android Library | Book browsing and favorite toggling implemented with **MVVM**. |
| `:feature:favorites` | Android Library | Saved books list and removal actions implemented with **MVP**. |
| `:feature:checkout` | Android Library | Interactive checkout flow with email validation and ordering via **MVI**. |

---

## 3. Dependency Rules & Boundaries

1. **Features Depend on Abstractions Only:** Features depend on `:data:books-api`, never on `:data:books-impl` or `:data:books-demo`.
2. **App as Composition Root & Mediator:** Only `:app` wires implementation modules to abstractions and mediates navigation between features using IDs and callbacks (`onBookClick`). Features have zero feature-to-feature dependencies.
3. **No Upward Dependencies:** Core and data modules never depend on feature modules or `:app`.
4. **Isolated Testing Module:** Production source sets never depend on `:core:testing`; it is consumed strictly via `testImplementation`.
5. **Minimal `api` Usage:** `implementation` is used by default. `api` is reserved for public signature exposures (e.g., `:data:books-api` exposing `:core:model` and Coroutines `Flow`).

---

## 4. Architectural Patterns in Detail

### 1. Catalog — MVVM (Model-View-ViewModel)
- **ViewModel (`CatalogViewModel`):** Merges `observeBooks()` and `observeFavoriteIds()` using `combine` into a single `StateFlow<CatalogUiState>`.
- **View (`CatalogScreen` / `CatalogRoute`):** Stateless Compose UI observing UI state with lifecycle awareness; forwards user events (`onFavoriteClick`) to the ViewModel.
- **Benefits:** Automatically retains state across configuration changes (e.g. screen rotations).

### 2. Favorites — MVP (Model-View-Presenter)
- **Contract (`FavoritesContract`):** Defines clear interfaces for `View` and `Presenter` with zero Android framework dependencies in the presenter.
- **Presenter (`FavoritesPresenter`):** Mediates between `BookRepository` and `View`, filtering favorite items and calling passive view methods (`showLoading()`, `showEmpty()`, `showFavorites()`).
- **View (`FavoritesViewState` + `FavoritesScreen`):** Dumb view holding observable Compose state driven exclusively by presenter commands.
- **Benefits:** Complete decoupling of presentation logic, verified on the JVM using a lightweight `RecordingView`.

### 3. Checkout — MVI (Model-View-Intent)
- **Single Source of Truth (`CheckoutState`):** Immutable data class modeling all screen states (loading, loaded, placing order, order completed, errors).
- **User & System Intents (`CheckoutIntent`):** Sealed interface describing every possible action or asynchronous result.
- **Pure Reducer (`CheckoutReducer`):** Deterministic pure function `(CheckoutState, CheckoutIntent) -> CheckoutState` that transitions states predictability.
- **Side Effects & Process Death:** `CheckoutViewModel` coordinates asynchronous repository calls, logs all state transitions, and restores user input (`email`) from `SavedStateHandle`.

---

## 5. Build Flavors

The project defines two product flavors under the `version` flavor dimension:
- **`full`:** Binds `BooksDataModule.provideRepository()` (`:data:books-impl`) providing the full interactive in-memory store.
- **`demo`:** Binds `DemoBooksDataModule.provideRepository()` (`:data:books-demo`) providing fixed sample books, configured with `applicationIdSuffix = ".demo"` to allow side-by-side device installations.

---

## 6. Verification & Testing

All unit tests run on the JVM without requiring an emulator:

```bash
# Run all unit tests across all modules
./gradlew test

# Assemble both debug flavors
./gradlew assembleDebug

# Run lint checks
./gradlew lint
```
