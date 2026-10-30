package com.example.menugo.viewmodel

import com.example.menugo.data.memory.MemoryDataRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductFormViewModelTest {

    @Test
    fun failedLoadClearsPreviousDataAndBlocksSaving() {
        val repository = MemoryDataRepository()
        val viewModel = ProductFormViewModel(repository)
        val originalProduct = requireNotNull(
            repository.getProductById("product-1")
        )
        val originalCount = repository.getProducts().size

        viewModel.loadProduct("product-1")
        viewModel.loadProduct("missing-product")

        val failedState = viewModel.uiState.value

        assertEquals("", failedState.id)
        assertEquals("", failedState.name)
        assertEquals("", failedState.description)
        assertEquals("", failedState.price)
        assertFalse(failedState.canSave)
        assertFalse(failedState.isSaved)
        assertEquals(
            ProductFormError.PRODUCT_NOT_FOUND,
            failedState.error
        )

        viewModel.updateName("Changed name")
        viewModel.updateDescription("Changed description")
        viewModel.updatePrice("4500")
        viewModel.clearError()
        viewModel.saveProduct()

        assertFalse(viewModel.uiState.value.isSaved)
        assertEquals(
            ProductFormError.PRODUCT_NOT_FOUND,
            viewModel.uiState.value.error
        )
        assertEquals(
            originalProduct,
            repository.getProductById("product-1")
        )
        assertEquals(
            originalCount,
            repository.getProducts().size
        )
    }

    @Test
    fun startingNewProductAllowsCreationAfterFailedLoad() {
        val repository = MemoryDataRepository()
        val viewModel = ProductFormViewModel(repository)
        val originalCount = repository.getProducts().size

        viewModel.loadProduct("missing-product")
        viewModel.startNewProduct()

        assertTrue(viewModel.uiState.value.canSave)
        assertFalse(viewModel.uiState.value.isEditing)
        assertEquals("", viewModel.uiState.value.id)

        viewModel.updateName("New Burger")
        viewModel.updateDescription("A new menu product.")
        viewModel.updatePrice("4500")
        viewModel.saveProduct()

        val state = viewModel.uiState.value
        val savedProduct = requireNotNull(
            repository.getProductById(state.id)
        )

        assertTrue(state.isSaved)
        assertEquals("New Burger", savedProduct.name)
        assertEquals(450000L, savedProduct.priceInCents)
        assertEquals(
            originalCount + 1,
            repository.getProducts().size
        )
    }

    @Test
    fun loadingValidProductRestoresEditingAfterFailedLoad() {
        val repository = MemoryDataRepository()
        val viewModel = ProductFormViewModel(repository)

        viewModel.loadProduct("missing-product")
        viewModel.loadProduct("product-1")

        assertTrue(viewModel.uiState.value.canSave)
        assertTrue(viewModel.uiState.value.isEditing)
        assertEquals("product-1", viewModel.uiState.value.id)

        viewModel.updateName("Updated Burger")
        viewModel.saveProduct()

        assertTrue(viewModel.uiState.value.isSaved)
        assertEquals(
            "Updated Burger",
            repository.getProductById("product-1")?.name
        )
    }

    @Test
    fun deletingProductDuringEditingBlocksFurtherSaving() {
        val repository = MemoryDataRepository()
        val viewModel = ProductFormViewModel(repository)

        viewModel.loadProduct("product-1")
        repository.deleteProduct("product-1")

        val countAfterDeletion = repository.getProducts().size

        viewModel.saveProduct()

        assertFalse(viewModel.uiState.value.canSave)
        assertFalse(viewModel.uiState.value.isSaved)
        assertEquals("", viewModel.uiState.value.id)
        assertEquals(
            ProductFormError.PRODUCT_NOT_FOUND,
            viewModel.uiState.value.error
        )

        viewModel.updateName("Replacement Burger")
        viewModel.updateDescription("Must not be saved.")
        viewModel.updatePrice("5000")
        viewModel.saveProduct()

        assertFalse(viewModel.uiState.value.isSaved)
        assertEquals(
            countAfterDeletion,
            repository.getProducts().size
        )
    }
}