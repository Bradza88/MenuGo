package com.example.menugo.data.memory

import com.example.menugo.domain.model.ProductCategory
import com.example.menugo.domain.model.ProductFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class MemoryDataRepositoryTest {

    @Test
    fun filtersCombineSearchCategoryPriceAndPromotion() {
        val repository = MemoryDataRepository()

        val filter = ProductFilter(
            query = " FRIES ",
            category = ProductCategory.SIDES,
            minPriceInCents = 150000L,
            maxPriceInCents = 200000L,
            onlyPromotions = true
        )

        val results = repository.getProducts(filter)

        assertEquals(1, results.size)
        assertEquals("product-2", results.single().id)

        val incompatibleFilter = filter.copy(
            category = ProductCategory.DRINKS
        )

        assertTrue(repository.getProducts(incompatibleFilter).isEmpty())
    }

    @Test
    fun addingTheSameProductAccumulatesQuantity() {
        val repository = MemoryDataRepository()

        repository.addToCart("product-1", 2)
        repository.addToCart("product-1", 1)

        val item = repository.getCartItems().single()

        assertEquals(3, item.quantity)
        assertEquals(900000L, item.subtotalInCents)
    }

    @Test
    fun productChangesKeepTheCartConsistent() {
        val repository = MemoryDataRepository()

        repository.addToCart("product-1", 2)

        val product = requireNotNull(
            repository.getProductById("product-1")
        )

        val updated = repository.updateProduct(
            product.copy(priceInCents = 350000L)
        )

        assertTrue(updated)
        assertEquals(
            700000L,
            repository.getCartItems().single().subtotalInCents
        )

        assertTrue(repository.deleteProduct("product-1"))
        assertNull(repository.getProductById("product-1"))
        assertTrue(repository.getCartItems().isEmpty())
    }

    @Test
    fun confirmedOrdersPreservePricesAfterCatalogChanges() {
        val repository = MemoryDataRepository()

        repository.addToCart("product-1", 2)

        val order = repository.createOrder("branch-1")

        assertTrue(repository.getCartItems().isEmpty())
        assertNotNull(repository.getOrderById(order.id))
        assertEquals(600000L, order.totalInCents)

        val product = requireNotNull(
            repository.getProductById("product-1")
        )

        repository.updateProduct(
            product.copy(priceInCents = 400000L)
        )
        repository.deleteProduct("product-1")

        val savedOrder = requireNotNull(
            repository.getOrderById(order.id)
        )

        assertEquals(
            300000L,
            savedOrder.items.single().unitPriceInCents
        )
        assertEquals(600000L, savedOrder.totalInCents)
    }

    @Test
    fun invalidBranchDoesNotClearTheCartOrCreateAnOrder() {
        val repository = MemoryDataRepository()

        repository.addToCart("product-1", 2)

        try {
            repository.createOrder("missing-branch")
            fail("An invalid branch must reject the order.")
        } catch (exception: IllegalArgumentException) {
            assertEquals(
                2,
                repository.getCartItems().single().quantity
            )
            assertTrue(repository.getOrders().isEmpty())
        }
    }
}