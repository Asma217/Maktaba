package com.ElOuedUniv.maktaba.presentation.book.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.domain.usecase.GetBookByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBookByIdUseCase: GetBookByIdUseCase
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle["isbn"])

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBook()
    }

    private fun loadBook() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val book = getBookByIdUseCase(bookId)
                _uiState.update { it.copy(isLoading = false, book = book) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onAction(action: BookDetailUiAction) {
        when (action) {
            BookDetailUiAction.OnBackClick -> {
                // handled by UI layer
            }
        }
    }
}
