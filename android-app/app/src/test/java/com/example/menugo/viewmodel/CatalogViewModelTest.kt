package com.example.menugo.viewmodel

import com.example.menugo.data.memory.MemoryDataRepository
import com.example.menugo.domain.model.ProductCategory
import com.example.menugo.domain.model.ProductFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogViewModelTest {

    @Test
    fun combinesSearchCategoryPriceAndPromotionFilters() {
        val repository = MemoryDataRepository()
        val viewModel = CatalogViewModel(repository)

        viewModel.search(" FRIES ")
        viewModel.selectCategory(ProductCategory.SIDES)
        viewModel.setMinPrice(150000L)
        viewModel.setMaxPrice(200000L)
        viewModel.setOnlyPromotions(true)

        val state = viewModel.uiState.value

        assertNull(state.error)
        assertEquals(1, state.products.size)
        assertEquals("product-2", state.products.single().id)
    }

    @Test
    fun clearingFiltersRestoresTheCatalog() {
        val repository = MemoryDataRepository()
        val viewModel = CatalogViewModel(repository)
        val initialProducts = viewModel.uiState.value.products

        viewModel.search("missing-product")
        viewModel.setOnlyPromotions(true)

        assertTrue(viewModel.uiState.value.isEmpty)

        viewModel.clearFilters()

        val state = viewModel.uiState.value

        assertEquals(ProductFilter(), state.filter)
        assertEquals(initialProducts, state.products)
        assertNull(state.error)
    }

    @Test
    fun refreshRemovesUnavailableProductsFromCustomerCatalog() {
        val repository = MemoryDataRepository()
        val viewModel = CatalogViewModel(repository)

        assertTrue(
            viewModel.uiState.value.products.any {
                it.id == "product-1"
            }
        )

        val product = requireNotNull(
            repository.getProductById("product-1")
        )

        repository.updateProduct(
            product.copy(isAvailable = false)
        )

        viewModel.refresh()

        assertTrue(
            viewModel.uiState.value.products.none {
                it.id == "product-1"
            }
        )

        assertEquals(
            false,
            repository.getProductById("product-1")?.isAvailable
        )
    }
}