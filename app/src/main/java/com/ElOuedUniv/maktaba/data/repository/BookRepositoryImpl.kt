package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Book
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor() : BookRepository {

    private val _booksList = mutableListOf(
        Book(isbn = "9780132350884", title = "Clean Code", author = "Robert C. Martin", nbPages = 464, imageUrl = "https://covers.openlibrary.org/b/isbn/9780132350884-L.jpg"),
        Book(isbn = "9780201616224", title = "The Pragmatic Programmer", author = "Andrew Hunt", nbPages = 352, imageUrl = "https://covers.openlibrary.org/b/isbn/9780201616224-L.jpg"),
        Book(isbn = "9780201633610", title = "Design Patterns", author = "Erich Gamma", nbPages = 395, imageUrl = "https://covers.openlibrary.org/b/isbn/9780201633610-L.jpg"),
    )

    private val booksFlow = MutableSharedFlow<List<Book>>(replay = 1).apply {
        tryEmit(_booksList.toList())
    }
    
    override fun getAllBooks(): Flow<List<Book>> = flow {
        delay(2000) // Simulate delay
        emitAll(booksFlow)
    }

    override suspend fun getBookByIsbn(isbn: String): Book? {
        delay(500)
        return _booksList.find { it.isbn == isbn }
    }

    override suspend fun addBook(book: Book, imageBytes: ByteArray?, pdfBytes: ByteArray?): Result<Unit> {
        return try {
            delay(1000)
            _booksList.add(book)
            booksFlow.tryEmit(_booksList.toList())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookById(id: String): Book? {
        return _booksList.find { it.id == id }
    }
}
