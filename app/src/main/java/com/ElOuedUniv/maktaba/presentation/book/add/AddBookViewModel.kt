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

    private var addBookJob: kotlinx.coroutines.Job? = null

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
            is AddBookUiAction.OnAddClick -> {
                if (_uiState.value.isFormValid) {
                    if (checkPdfSize(action.pdfBytes)) {
                        addBook(action.imageBytes, action.pdfBytes)
                    }
                }
            }
        }
    }

    private fun validateInputs() {
        val title = _uiState.value.title
        val isbn = _uiState.value.isbn
        val nbPages = _uiState.value.nbPages

        val titleError = if (title.isBlank()) "Title cannot be empty" else null
        val isbnError = if (isbn.length != 13 || isbn.any { !it.isDigit() }) "ISBN must be 13 digits" else null
        val pagesInt = nbPages.toIntOrNull()
        val pagesError = if (pagesInt == null || pagesInt <= 0) "Pages must be a positive number" else null

        _uiState.update {
            it.copy(
                titleError = titleError,
                isbnError = isbnError,
                nbPagesError = pagesError,
                isFormValid = titleError == null && isbnError == null && pagesError == null
            )
        }
    }

    private fun checkPdfSize(pdfBytes: ByteArray?): Boolean {
        if (pdfBytes == null) {
            _uiState.update {
                it.copy(errorMessage = "الرجاء اختيار ملف PDF أولاً")
            }
            return false
        }

        val sizeMB = pdfBytes.size / (1024.0 * 1024.0)

        return when {
            sizeMB > 30 -> {
                _uiState.update {
                    it.copy(errorMessage = "الكتاب كبير جداً (%.1f MB). الحد الأقصى 30 MB".format(sizeMB))
                }
                false
            }
            sizeMB > 15 -> {
                _uiState.update {
                    it.copy(errorMessage = "⚠️ الكتاب كبير (%.1f MB). الرفع قد ياخذ دقيقة أو أكثر".format(sizeMB))
                }
                true
            }
            else -> true
        }
    }

    private fun addBook(imageBytes: ByteArray? = null, pdfBytes: ByteArray? = null) {
        addBookJob?.cancel()

        val currentState = _uiState.value

        pdfBytes?.let {
            val sizeMB = it.size / (1024.0 * 1024.0)
            android.util.Log.d("MAKTABA", "📚 رفع PDF بحجم: %.2f MB".format(sizeMB))
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                isSuccess = false,
                errorMessage = null
            )
        }

        addBookJob = viewModelScope.launch {
            val result = addBookUseCase(
                Book(
                    isbn = currentState.isbn,
                    title = currentState.title,
                    author = "Unknown",
                    nbPages = currentState.nbPages.toIntOrNull() ?: 0,
                    imageUrl = "",
                    pdfUrl = ""
                ),
                imageBytes,
                pdfBytes
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = "❌ فشل الرفع: ${exception.message ?: "خطأ غير معروف"}"
                        )
                    }
                }
            )
        }
    }

    fun cancelUpload() {
        addBookJob?.cancel()
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = "تم إلغاء الرفع"
            )
        }
    }
}
