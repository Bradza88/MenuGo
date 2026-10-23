
package com.example.menugo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.menugo.domain.model.Product
import com.example.menugo.domain.model.ProductCategory
import com.example.menugo.domain.model.ProductFilter
import com.example.menugo.domain.repository.MenuGoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class CatalogError {
    LOAD_FAILED
}

data class CatalogUiState(
    val products: List<Product> = emptyList(),
    val filter: ProductFilter = ProductFilter(),
    val error: CatalogError? = null
) {
    val isEmpty: Boolean
        get() = products.isEmpty()
}

class CatalogViewModel(
    private val repository: MenuGoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        loadWithFilter(_uiState.value.filter)
    }

    fun search(query: String) {
        updateFilter { copy(query = query) }
    }

    fun selectCategory(category: ProductCategory?) {
        updateFilter { copy(category = category) }
    }

    fun setMinPrice(priceInCents: Long?) {
        updateFilter { copy(minPriceInCents = priceInCents) }
    }

    fun setMaxPrice(priceInCents: Long?) {
        updateFilter { copy(maxPriceInCents = priceInCents) }
    }

    fun setOnlyPromotions(enabled: Boolean) {
        updateFilter { copy(onlyPromotions = enabled) }
    }

    fun clearFilters() {
        loadWithFilter(ProductFilter())
    }

    fun refresh() {
        loadProducts()
    }

    private fun updateFilter(
        change: ProductFilter.() -> ProductFilter
    ) {
        val newFilter = _uiState.value.filter.change()
        loadWithFilter(newFilter)
    }

    private fun loadWithFilter(filter: ProductFilter) {
        _uiState.value = _uiState.value.copy(
            filter = filter,
            error = null
        )

        try {
            val products = repository.getProducts(filter)
                .filter { it.isAvailable }

            _uiState.value = _uiState.value.copy(
                products = products,
                error = null
            )
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                products = emptyList(),
                error = CatalogError.LOAD_FAILED
            )
        }
    }
}
