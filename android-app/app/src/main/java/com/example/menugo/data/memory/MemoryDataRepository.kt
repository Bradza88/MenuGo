package com.example.menugo.data.memory

import com.example.menugo.domain.model.Branch
import com.example.menugo.domain.model.CartItem
import com.example.menugo.domain.model.Order
import com.example.menugo.domain.model.OrderStatus
import com.example.menugo.domain.model.Product
import com.example.menugo.domain.model.ProductCategory
import com.example.menugo.domain.model.ProductFilter
import com.example.menugo.domain.repository.MenuGoRepository
import com.example.menugo.domain.model.OrderItem
import java.util.UUID

class MemoryDataRepository : MenuGoRepository {

    private val products = mutableListOf(
        Product(
            id = "product-1",
            name = "Classic Burger",
            description = "Beef, cheese, lettuce and tomato.",
            priceInCents = 300000L,
            category = ProductCategory.HAMBURGERS
        ),
        Product(
            id = "product-2",
            name = "French Fries",
            description = "A portion of crispy french fries.",
            priceInCents = 180000L,
            category = ProductCategory.SIDES,
            isOnPromotion = true
        ),
        Product(
            id = "product-3",
            name = "Soft Drink",
            description = "A medium soft drink.",
            priceInCents = 120000L,
            category = ProductCategory.DRINKS
        ),
        Product(
            id = "product-4",
            name = "Chocolate Sundae",
            description = "Vanilla ice cream with chocolate sauce.",
            priceInCents = 150000L,
            category = ProductCategory.DESSERTS
        )
    )

    private val branches = listOf(
        Branch(
            id = "branch-1",
            name = "MenuGo Demo Center",
            address = "Sample pickup address A",
            latitude = 9.935,
            longitude = -84.084,
            openingHours = "11:00 - 22:00"
        ),
        Branch(
            id = "branch-2",
            name = "MenuGo Demo North",
            address = "Sample pickup address B",
            latitude = 9.945,
            longitude = -84.084,
            openingHours = "11:00 - 21:00"
        )
    )

    private val cartItems = mutableListOf<CartItem>()

    private val orders = mutableListOf<Order>()

    // Product catalog

    override fun getProducts(filter: ProductFilter): List<Product> {
        validateFilter(filter)

        val query = filter.query.trim()

        return products.filter { product ->
            val matchesQuery =
                query.isBlank() ||
                        product.name.contains(query, ignoreCase = true)

            val matchesCategory =
                filter.category == null ||
                        product.category == filter.category

            val matchesMinPrice =
                filter.minPriceInCents == null ||
                        product.priceInCents >= filter.minPriceInCents

            val matchesMaxPrice =
                filter.maxPriceInCents == null ||
                        product.priceInCents <= filter.maxPriceInCents

            val matchesPromotion =
                !filter.onlyPromotions || product.isOnPromotion

            matchesQuery &&
                    matchesCategory &&
                    matchesMinPrice &&
                    matchesMaxPrice &&
                    matchesPromotion
        }
    }

    override fun getProductById(productId: String): Product? {
        return products.find { it.id == productId }
    }

    override fun addProduct(product: Product) {
        validateProduct(product)

        require(products.none { it.id == product.id }) {
            "A product with this ID already exists."
        }

        products.add(product)
    }

    override fun updateProduct(product: Product): Boolean {
        validateProduct(product)

        val index = products.indexOfFirst { it.id == product.id }

        if (index == -1) {
            return false
        }

        products[index] = product

        if (product.isAvailable) {
            for (cartIndex in cartItems.indices) {
                val item = cartItems[cartIndex]

                if (item.product.id == product.id) {
                    cartItems[cartIndex] = item.copy(product = product)
                }
            }
        } else {
            cartItems.removeAll { it.product.id == product.id }
        }

        return true
    }

    override fun deleteProduct(productId: String): Boolean {
        val removed = products.removeAll { it.id == productId }

        if (removed) {
            cartItems.removeAll { it.product.id == productId }
        }

        return removed
    }

    // Pickup branches

    override fun getBranches(): List<Branch> {
        return branches.filter { it.isActive }
    }

    override fun getBranchById(branchId: String): Branch? {
        return branches.find { it.id == branchId }
    }

    // Shopping cart

    override fun getCartItems(): List<CartItem> {
        return cartItems.toList()
    }

    override fun addToCart(productId: String, quantity: Int) {
        require(quantity > 0) {
            "Quantity must be greater than zero."
        }

        val product = requireNotNull(getProductById(productId)) {
            "Product not found."
        }

        require(product.isAvailable) {
            "Product is not available."
        }

        val index = cartItems.indexOfFirst {
            it.product.id == productId
        }

        if (index == -1) {
            cartItems.add(
                CartItem(
                    product = product,
                    quantity = quantity
                )
            )
        } else {
            val currentItem = cartItems[index]

            val updatedQuantity = Math.addExact(
                currentItem.quantity,
                quantity
            )

            cartItems[index] = currentItem.copy(
                product = product,
                quantity = updatedQuantity
            )
        }
    }

    override fun updateCartItemQuantity(
        productId: String,
        quantity: Int
    ): Boolean {
        require(quantity > 0) {
            "Quantity must be greater than zero."
        }

        val index = cartItems.indexOfFirst {
            it.product.id == productId
        }

        if (index == -1) {
            return false
        }

        cartItems[index] = cartItems[index].copy(
            quantity = quantity
        )

        return true
    }

    override fun removeFromCart(productId: String): Boolean {
        return cartItems.removeAll {
            it.product.id == productId
        }
    }

    override fun clearCart() {
        cartItems.clear()
    }

    // Orders

    override fun createOrder(branchId: String): Order {
        require(cartItems.isNotEmpty()) {
            "The cart must not be empty."
        }

        val branch = requireNotNull(getBranchById(branchId)) {
            "Branch not found."
        }

        require(branch.isActive) {
            "Branch is not active."
        }

        val orderItems = cartItems.map { cartItem ->
            val product = requireNotNull(
                getProductById(cartItem.product.id)
            ) {
                "A product in the cart no longer exists."
            }

            require(product.isAvailable) {
                "A product in the cart is not available."
            }

            OrderItem(
                productId = product.id,
                productName = product.name,
                unitPriceInCents = product.priceInCents,
                quantity = cartItem.quantity
            )
        }

        val order = Order(
            id = UUID.randomUUID().toString(),
            items = orderItems,
            branch = branch,
            createdAtEpochMillis = System.currentTimeMillis()
        )

        orders.add(order)
        clearCart()

        return order
    }

    override fun getOrders(): List<Order> {
        return orders.toList()
    }

    override fun getOrderById(orderId: String): Order? {
        return orders.find {
            it.id == orderId
        }
    }

    override fun updateOrderStatus(
        orderId: String,
        status: OrderStatus
    ): Boolean {
        val index = orders.indexOfFirst {
            it.id == orderId
        }

        if (index == -1) {
            return false
        }

        orders[index] = orders[index].copy(
            status = status
        )

        return true
    }
    // Validation

    private fun validateProduct(product: Product) {
        require(product.id.isNotBlank()) {
            "Product ID must not be blank."
        }

        require(product.name.isNotBlank()) {
            "Product name must not be blank."
        }

        require(product.description.isNotBlank()) {
            "Product description must not be blank."
        }

        require(product.priceInCents >= 0) {
            "Product price must not be negative."
        }
    }

    private fun validateFilter(filter: ProductFilter) {
        require(
            filter.minPriceInCents == null ||
                    filter.minPriceInCents >= 0
        ) {
            "Minimum price must not be negative."
        }

        require(
            filter.maxPriceInCents == null ||
                    filter.maxPriceInCents >= 0
        ) {
            "Maximum price must not be negative."
        }

        if (
            filter.minPriceInCents != null &&
            filter.maxPriceInCents != null
        ) {
            require(filter.minPriceInCents <= filter.maxPriceInCents) {
                "Minimum price must not exceed maximum price."
            }
        }
    }
}