package com.example.menugo.domain.repository

import com.example.menugo.domain.model.Branch
import com.example.menugo.domain.model.CartItem
import com.example.menugo.domain.model.Order
import com.example.menugo.domain.model.OrderStatus
import com.example.menugo.domain.model.Product
import com.example.menugo.domain.model.ProductFilter

interface MenuGoRepository {

    // Product catalog

    fun getProducts(
        filter: ProductFilter = ProductFilter()
    ): List<Product>

    fun getProductById(productId: String): Product?

    fun addProduct(product: Product)

    fun updateProduct(product: Product): Boolean

    fun deleteProduct(productId: String): Boolean

    // Pickup branches

    fun getBranches(): List<Branch>

    fun getBranchById(branchId: String): Branch?

    // Shopping cart

    fun getCartItems(): List<CartItem>

    fun addToCart(
        productId: String,
        quantity: Int = 1
    )

    fun updateCartItemQuantity(
        productId: String,
        quantity: Int
    ): Boolean

    fun removeFromCart(productId: String): Boolean

    fun clearCart()

    // Orders

    fun createOrder(branchId: String): Order

    fun getOrders(): List<Order>

    fun getOrderById(orderId: String): Order?

    fun updateOrderStatus(
        orderId: String,
        status: OrderStatus
    ): Boolean
}