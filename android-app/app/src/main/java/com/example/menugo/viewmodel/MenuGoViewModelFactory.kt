package com.example.menugo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.menugo.data.memory.RepositoryProvider
import com.example.menugo.domain.repository.MenuGoRepository

class MenuGoViewModelFactory(
    private val repository: MenuGoRepository = RepositoryProvider.repository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CatalogViewModel::class.java) ->
                CatalogViewModel(repository) as T

            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) ->
                ProductDetailViewModel(repository) as T

            modelClass.isAssignableFrom(CartViewModel::class.java) ->
                CartViewModel(repository) as T

            modelClass.isAssignableFrom(BranchViewModel::class.java) ->
                BranchViewModel(repository) as T

            modelClass.isAssignableFrom(AdminProductViewModel::class.java) ->
                AdminProductViewModel(repository) as T

            modelClass.isAssignableFrom(ProductFormViewModel::class.java) ->
                ProductFormViewModel(repository) as T

            else -> throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}"
            )
        }
    }
}
