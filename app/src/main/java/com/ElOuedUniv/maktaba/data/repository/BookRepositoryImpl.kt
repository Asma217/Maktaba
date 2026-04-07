package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Book
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor() : BookRepository {

    private val _booksList = mutableListOf<Book>(
        Book(isbn = "9780132764094", title = "Clean Code", nbPages = 496),
        Book(isbn = "9782322017225", title = "The Pragmatic Programmer", nbPages = 540),
        Book(isbn = "9781484236215", title = " Design Patterns", nbPages = 207),
        Book(isbn = "9780596008741", title = "Refactoring", nbPages = 220),
        Book(isbn = "9780596007126", title = "Head First Design Patterns", nbPages =638),
        Book(isbn = "9780134171500", title = "Android Programming: The Big Nerd Ranch Guide", nbPages = 600),
        Book(isbn = "9780136870487", title = "Kotlin Programming: The Big Nerd Ranch Guide", nbPages = 600),
        Book(isbn = "9781491903117", title = "Designing Data-Intensive Applications", nbPages = 616),
        Book(isbn = "9780132778046", title = "Effective Java", nbPages = 384),
        Book(isbn = "9781430263234", title = "Android Cookbook", nbPages = 772),
        Book(isbn = "9781492056300", title = "Fluent Python", nbPages = 1014),
        Book(isbn = "9783031161278", title = "Learning SQL", nbPages = 291),
        Book(isbn = "9781394217274", title = "Automate the Boring Stuff with Python", nbPages = 272),
        Book(isbn = "9781484244678", title = "Learning Kotlin", nbPages = 508),
        Book(isbn = "9781638353690", title = "Kotlin in Action", nbPages = 360),
    )

    private val booksFlow = MutableSharedFlow<List<Book>>(replay = 1).apply {
        tryEmit(_booksList)
    }

    override fun getAllBooks(): Flow<List<Book>> = flow {
        delay(2000) // Simulate delay
        emitAll(booksFlow)
    }

    override fun getBookByIsbn(isbn: String): Book? {
        return _booksList.find { it.isbn == isbn }
    }

    override fun addBook(book: Book) {
        _booksList.add(book)
        booksFlow.tryEmit(_booksList.toList())
    }


}