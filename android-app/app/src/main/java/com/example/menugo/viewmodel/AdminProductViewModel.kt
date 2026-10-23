package com.example.menugo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.menugo.domain.model.Product
import com.example.menugo.domain.repository.MenuGoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AdminProductError {
    LOAD_FAILED,
    DELETE_FAILED
}

data class AdminProductUiState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val error: AdminProductError? = null
)

class AdminProductViewModel(
    private val repository: MenuGoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminProductUiState())
    val uiState: StateFlow<AdminProductUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        try {
            val products = repository.getProducts()

            _uiState.value = _uiState.value.copy(
                products = filterProducts(
                    products = products,
                    query = _uiState.value.searchQuery
                ),
                error = null
            )
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                error = AdminProductError.LOAD_FAILED
            )
        }
    }

    fun search(query: String) {
        val allProducts = try {
            repository.getProducts()
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                searchQuery = query,
                error = AdminProductError.LOAD_FAILED
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            products = filterProducts(allProducts, query),
            searchQuery = query,
            error = null
        )
    }

    fun deleteProduct(productId: String) {
        try {
            val deleted = repository.deleteProduct(productId)

            if (deleted) {
                loadProducts()
            } else {
                _uiState.value = _uiState.value.copy(
                    error = AdminProductError.DELETE_FAILED
                )
            }
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                error = AdminProductError.DELETE_FAILED
            )
        }
    }

    fun refresh() {
        loadProducts()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun filterProducts(
        products: List<Product>,
        query: String
    ): List<Product> {
        val normalizedQuery = query.trim()

        if (normalizedQuery.isEmpty()) {
            return products
        }

        return products.filter { product ->
            product.name.contains(
                other = normalizedQuery,
                ignoreCase = true
            )
        }
    }
}
