
package com.example.menugo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.menugo.domain.model.Product
import com.example.menugo.domain.model.ProductCategory
import com.example.menugo.domain.repository.MenuGoRepository
import com.example.menugo.util.ProductValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ProductFormError {
    NAME_REQUIRED,
    DESCRIPTION_REQUIRED,
    INVALID_PRICE,
    PRODUCT_NOT_FOUND,
    DUPLICATE_ID,
    SAVE_FAILED
}

data class ProductFormUiState(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val category: ProductCategory = ProductCategory.HAMBURGERS,
    val imageUri: String = "",
    val isOnPromotion: Boolean = false,
    val isAvailable: Boolean = true,
    val isEditing: Boolean = false,
    val error: ProductFormError? = null,
    val isSaved: Boolean = false
)

class ProductFormViewModel(
    private val repository: MenuGoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductFormUiState())
    val uiState: StateFlow<ProductFormUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        try {
            val product = repository.getProductById(productId)

            if (product == null) {
                _uiState.value = _uiState.value.copy(
                    error = ProductFormError.PRODUCT_NOT_FOUND
                )
                return
            }

            _uiState.value = ProductFormUiState(
                id = product.id,
                name = product.name,
                description = product.description,
                price = centsToPriceString(product.priceInCents),
                category = product.category,
                imageUri = product.imageUri.orEmpty(),
                isOnPromotion = product.isOnPromotion,
                isAvailable = product.isAvailable,
                isEditing = true
            )
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                error = ProductFormError.PRODUCT_NOT_FOUND
            )
        }
    }

    fun updateName(value: String) {
        updateForm { copy(name = value, error = null, isSaved = false) }
    }

    fun updateDescription(value: String) {
        updateForm { copy(description = value, error = null, isSaved = false) }
    }

    fun updatePrice(value: String) {
        updateForm { copy(price = value, error = null, isSaved = false) }
    }

    fun updateCategory(value: ProductCategory) {
        updateForm { copy(category = value, error = null, isSaved = false) }
    }

    fun updateImageUri(value: String) {
        updateForm { copy(imageUri = value, error = null, isSaved = false) }
    }

    fun setPromotion(enabled: Boolean) {
        updateForm { copy(isOnPromotion = enabled, error = null, isSaved = false) }
    }

    fun setAvailable(enabled: Boolean) {
        updateForm { copy(isAvailable = enabled, error = null, isSaved = false) }
    }

    fun saveProduct() {
        val form = _uiState.value

        if (!ProductValidationUtils.validateRequiredText(form.name)) {
            setError(ProductFormError.NAME_REQUIRED)
            return
        }

        if (!ProductValidationUtils.validateRequiredText(form.description)) {
            setError(ProductFormError.DESCRIPTION_REQUIRED)
            return
        }

        val priceInCents = parsePriceToCents(form.price)

        if (priceInCents == null || priceInCents < 0L) {
            setError(ProductFormError.INVALID_PRICE)
            return
        }

        val productId = if (form.isEditing) {
            form.id
        } else {
            java.util.UUID.randomUUID().toString()
        }

        val product = Product(
            id = productId,
            name = form.name.trim(),
            description = form.description.trim(),
            priceInCents = priceInCents,
            category = form.category,
            imageUri = form.imageUri.trim().ifEmpty { null },
            isOnPromotion = form.isOnPromotion,
            isAvailable = form.isAvailable
        )

        try {
            if (form.isEditing) {
                val updated = repository.updateProduct(product)

                if (!updated) {
                    setError(ProductFormError.PRODUCT_NOT_FOUND)
                    return
                }
            } else {
                repository.addProduct(product)
            }

            _uiState.value = _uiState.value.copy(
                id = productId,
                isEditing = true,
                error = null,
                isSaved = true
            )
        } catch (exception: IllegalArgumentException) {
            setError(
                if (!form.isEditing) {
                    ProductFormError.DUPLICATE_ID
                } else {
                    ProductFormError.SAVE_FAILED
                }
            )
        } catch (exception: Exception) {
            setError(ProductFormError.SAVE_FAILED)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun updateForm(
        change: ProductFormUiState.() -> ProductFormUiState
    ) {
        _uiState.value = _uiState.value.change()
    }

    private fun setError(error: ProductFormError) {
        _uiState.value = _uiState.value.copy(
            error = error,
            isSaved = false
        )
    }

    private fun parsePriceToCents(value: String): Long? {
        return ProductValidationUtils.priceToCents(value)
    }

    private fun centsToPriceString(cents: Long): String {
        val whole = cents / 100
        val fraction = (cents % 100).toString().padStart(2, '0')
        return "$whole.$fraction"
    }
}
