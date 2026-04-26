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

    override suspend fun getBookById(id: String): Book? {
        return try {
            client.postgrest["books"]
                .select {
                    filter { eq("id", id) }
                }
                .decodeSingleOrNull<Book>()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addBook(book: Book, imageBytes: ByteArray?, pdfBytes: ByteArray?): Result<Unit> = try {

        var finalImageUrl = ""
        var finalPdfUrl = ""

        // رفع الصورة
        if (imageBytes != null && imageBytes.isNotEmpty()) {
            val fileName = "cover_${System.currentTimeMillis()}.jpg"
            client.storage["book_cover"].upload(fileName, imageBytes)
            finalImageUrl = client.storage["book_cover"].publicUrl(fileName)
            android.util.Log.d("MAKTABA", "✅ Image URL: $finalImageUrl")
        }

        // رفع الـ PDF
        if (pdfBytes != null && pdfBytes.isNotEmpty()) {
            val fileName = "book_${System.currentTimeMillis()}.pdf"
            client.storage["book_pdfs"].upload(fileName, pdfBytes)
            finalPdfUrl = client.storage["book_pdfs"].publicUrl(fileName)
            android.util.Log.d("MAKTABA", "✅ PDF URL: $finalPdfUrl")
        }

        val bookToInsert = book.copy(
            id = null,
            imageUrl = finalImageUrl,  // ← صح
            pdfUrl = finalPdfUrl
        )

        client.postgrest["books"].insert(bookToInsert)
        Result.success(Unit)

    } catch (e: Exception) {
        android.util.Log.e("MAKTABA", "❌ FAILED: ${e.message}")
        Result.failure(e)
    }
}
