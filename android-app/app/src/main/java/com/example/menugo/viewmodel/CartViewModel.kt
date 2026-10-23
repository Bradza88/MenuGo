
package com.example.menugo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.menugo.domain.model.CartItem
import com.example.menugo.domain.model.Branch
import com.example.menugo.domain.model.Order
import com.example.menugo.domain.model.OrderStatus
import com.example.menugo.domain.repository.MenuGoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class CartError {
    LOAD_FAILED,
    ADD_FAILED,
    UPDATE_FAILED,
    REMOVE_FAILED,
    CLEAR_FAILED,
    BRANCHES_LOAD_FAILED,
    BRANCH_REQUIRED,
    INVALID_BRANCH,
    EMPTY_CART,
    ORDER_FAILED
}

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val branches: List<Branch> = emptyList(),
    val selectedBranchId: String? = null,
    val createdOrder: Order? = null,
    val error: CartError? = null
) {
    val isEmpty: Boolean
        get() = items.isEmpty()

    val totalInCents: Long
        get() = items.sumOf { item ->
            item.subtotalInCents
        }
}

class CartViewModel(
    private val repository: MenuGoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
        loadBranches()
    }

    fun loadCart() {
        try {
            _uiState.value = _uiState.value.copy(
                items = repository.getCartItems(),
                error = null
            )
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                error = CartError.LOAD_FAILED
            )
        }
    }

    fun loadBranches() {
        try {
            val branches = repository.getBranches()
                .filter { it.isActive }

            val currentSelection = _uiState.value.selectedBranchId
            val selectedBranch = branches.firstOrNull {
                it.id == currentSelection
            } ?: branches.firstOrNull()

            _uiState.value = _uiState.value.copy(
                branches = branches,
                selectedBranchId = selectedBranch?.id,
                error = null
            )
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                error = CartError.BRANCHES_LOAD_FAILED
            )
        }
    }

    fun selectBranch(branchId: String) {
        val branch = _uiState.value.branches.firstOrNull {
            it.id == branchId && it.isActive
        }

        if (branch == null) {
            _uiState.value = _uiState.value.copy(
                error = CartError.INVALID_BRANCH
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            selectedBranchId = branch.id,
            error = null
        )
    }

    fun addProduct(productId: String, quantity: Int = 1) {
        try {
            repository.addToCart(productId, quantity)
            loadCart()
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                error = CartError.ADD_FAILED
            )
        }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        try {
            val updated = repository.updateCartItemQuantity(
                productId = productId,
                quantity = quantity
            )

            if (updated) {
                loadCart()
            } else {
                _uiState.value = _uiState.value.copy(
                    error = CartError.UPDATE_FAILED
                )
            }
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                error = CartError.UPDATE_FAILED
            )
        }
    }

    fun removeProduct(productId: String) {
        try {
            val removed = repository.removeFromCart(productId)

            if (removed) {
                loadCart()
            } else {
                _uiState.value = _uiState.value.copy(
                    error = CartError.REMOVE_FAILED
                )
            }
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                error = CartError.REMOVE_FAILED
            )
        }
    }

    fun clearCart() {
        try {
            repository.clearCart()
            loadCart()
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                error = CartError.CLEAR_FAILED
            )
        }
    }

    fun createOrder() {
        val state = _uiState.value

        if (state.items.isEmpty()) {
            _uiState.value = state.copy(
                error = CartError.EMPTY_CART
            )
            return
        }

        val branchId = state.selectedBranchId
        if (branchId.isNullOrBlank()) {
            _uiState.value = state.copy(
                error = CartError.BRANCH_REQUIRED
            )
            return
        }

        val validBranch = state.branches.any {
            it.id == branchId && it.isActive
        }

        if (!validBranch) {
            _uiState.value = state.copy(
                error = CartError.INVALID_BRANCH
            )
            return
        }

        try {
            val order = repository.createOrder(branchId)

            _uiState.value = _uiState.value.copy(
                createdOrder = order,
                items = emptyList(),
                error = null
            )
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                error = CartError.ORDER_FAILED
            )
        }
    }

    fun clearCreatedOrder() {
        _uiState.value = _uiState.value.copy(
            createdOrder = null
        )
    }

    fun refresh() {
        loadCart()
        loadBranches()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            error = null
        )
    }
}
