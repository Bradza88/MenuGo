
package com.example.menugo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.menugo.domain.model.Product
import com.example.menugo.domain.repository.MenuGoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ProductDetailError {
    PRODUCT_NOT_FOUND,
    PRODUCT_UNAVAILABLE,
    INVALID_QUANTITY,
    ADD_TO_CART_FAILED
}

data class ProductDetailUiState(
    val product: Product? = null,
    val quantity: Int = 1,
    val error: ProductDetailError? = null,
    val addedToCart: Boolean = false
)

class ProductDetailViewModel(
    private val repository: MenuGoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        try {
            val product = repository.getProductById(productId)

            _uiState.value = _uiState.value.copy(
                product = product,
                error = if (product == null) {
                    ProductDetailError.PRODUCT_NOT_FOUND
                } else if (!product.isAvailable) {
                    ProductDetailError.PRODUCT_UNAVAILABLE
                } else {
                    null
                },
                addedToCart = false
            )
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                product = null,
                error = ProductDetailError.PRODUCT_NOT_FOUND,
                addedToCart = false
            )
        }
    }

    fun setQuantity(quantity: Int) {
        _uiState.value = _uiState.value.copy(
            quantity = quantity,
            error = if (quantity <= 0) {
                ProductDetailError.INVALID_QUANTITY
            } else {
                null
            },
            addedToCart = false
        )
    }

    fun addToCart() {
        val state = _uiState.value
        val product = state.product

        if (state.quantity <= 0) {
            _uiState.value = state.copy(
                error = ProductDetailError.INVALID_QUANTITY,
                addedToCart = false
            )
            return
        }

        if (product == null) {
            _uiState.value = state.copy(
                error = ProductDetailError.PRODUCT_NOT_FOUND,
                addedToCart = false
            )
            return
        }

        try {
            val currentProduct = repository.getProductById(product.id)

            if (currentProduct == null) {
                _uiState.value = state.copy(
                    error = ProductDetailError.PRODUCT_NOT_FOUND,
                    addedToCart = false
                )
                return
            }

            if (!currentProduct.isAvailable) {
                _uiState.value = state.copy(
                    product = currentProduct,
                    error = ProductDetailError.PRODUCT_UNAVAILABLE,
                    addedToCart = false
                )
                return
            }

            repository.addToCart(currentProduct.id, state.quantity)

            _uiState.value = state.copy(
                product = currentProduct,
                error = null,
                addedToCart = true
            )
        } catch (exception: Exception) {
            _uiState.value = state.copy(
                error = ProductDetailError.ADD_TO_CART_FAILED,
                addedToCart = false
            )
        }
    }
}
