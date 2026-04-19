package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Book
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SupabaseBookRepositoryImpl @Inject constructor(
    private val client: SupabaseClient
) : BookRepository {

    override fun getAllBooks(): Flow<List<Book>> = flow {
        val books = client.postgrest["books"]
            .select()
            .decodeList<Book>()
        emit(books)
    }

    override suspend fun getBookByIsbn(isbn: String): Book? {
        return client.postgrest["books"]
            .select {
                filter {
                    eq("isbn", isbn)
                }
            }
            .decodeSingleOrNull<Book>()
    }

    override suspend fun addBook(book: Book, imageBytes: ByteArray?) {
        var finalImageUrl = book.imageUrl

        if (imageBytes != null) {
            try {
                val fileName = "cover_${System.currentTimeMillis()}.jpg"
                client.storage["book_cover"].upload(fileName, imageBytes)
                finalImageUrl = client.storage["book_cover"].publicUrl(fileName)
            } catch (e: Exception) {

                finalImageUrl = ""
            }
        }

        // تم حذف id = null لأن الـ Book model لا يسمح بـ null للـ id (String)
        client.postgrest["books"].insert(book.copy(imageUrl = finalImageUrl))
    }
}
