package com.example.menugo.data.memory

import com.example.menugo.domain.repository.MenuGoRepository

object RepositoryProvider {

    val repository: MenuGoRepository by lazy {
        MemoryDataRepository()
    }
}