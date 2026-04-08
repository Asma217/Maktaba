package com.ElOuedUniv.maktaba.presentation.book.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.data.model.Book
import com.ElOuedUniv.maktaba.domain.usecase.AddBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val addBookUseCase: AddBookUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddBookUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: AddBookUiAction) {
        when (action) {
            is AddBookUiAction.OnTitleChange -> {
                _uiState.update { it.copy(title = action.title) }
                validateInputs()
            }
            is AddBookUiAction.OnIsbnChange -> {
                _uiState.update { it.copy(isbn = action.isbn) }
                validateInputs()
            }
            is AddBookUiAction.OnPagesChange -> {
                _uiState.update { it.copy(nbPages = action.pages) }
                validateInputs()
            }
            AddBookUiAction.OnAddClick -> {
                if (_uiState.value.isFormValid) addBook()
            }
        }
    }

    private fun validateInputs() {
        val state = _uiState.value

        val titleError = if (state.title.isBlank()) "Title cannot be empty" else null

        val isbnError = when {
            state.isbn.isBlank() -> "ISBN cannot be empty"
            !state.isbn.all { it.isDigit() } -> "ISBN must contain digits only"
            state.isbn.length != 13 -> "ISBN must be exactly 13 digits"
            else -> null
        }

        val pagesError = when {
            state.nbPages.isBlank() -> "Pages cannot be empty"
            state.nbPages.toIntOrNull() == null -> "Pages must be a number"
            state.nbPages.toInt() <= 0 -> "Pages must be a positive number"
            else -> null
        }

        _uiState.update {
            it.copy(
                titleError = titleError,
                isbnError = isbnError,
                pagesError = pagesError,
                isFormValid = titleError == null && isbnError == null && pagesError == null
            )
        }
    }

    private fun addBook() {
        val state = _uiState.value
        viewModelScope.launch {
            val book = Book(
                isbn = state.isbn,
                title = state.title,
                nbPages = state.nbPages.toInt()
            )
            addBookUseCase(book)
            _uiState.update { it.copy(isSuccess = true) }
        }
    }
}
