package com.example.menugo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.menugo.domain.model.Branch
import com.example.menugo.domain.repository.MenuGoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class BranchError {
    LOAD_FAILED,
    BRANCH_NOT_FOUND
}

data class BranchUiState(
    val branches: List<Branch> = emptyList(),
    val selectedBranch: Branch? = null,
    val error: BranchError? = null
)

class BranchViewModel(
    private val repository: MenuGoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BranchUiState())
    val uiState: StateFlow<BranchUiState> = _uiState.asStateFlow()

    init {
        loadBranches()
    }

    fun loadBranches() {
        try {
            _uiState.value = _uiState.value.copy(
                branches = repository.getBranches(),
                error = null
            )
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                error = BranchError.LOAD_FAILED
            )
        }
    }

    fun selectBranch(branchId: String) {
        try {
            val branch = repository.getBranchById(branchId)

            _uiState.value = _uiState.value.copy(
                selectedBranch = branch,
                error = if (branch == null) {
                    BranchError.BRANCH_NOT_FOUND
                } else {
                    null
                }
            )
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                selectedBranch = null,
                error = BranchError.LOAD_FAILED
            )
        }
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            selectedBranch = null,
            error = null
        )
    }

    fun refresh() {
        loadBranches()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}