package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Category
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SupabaseCategoryRepositoryImpl @Inject constructor(
    private val client: SupabaseClient
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> = flow {
        val categories = client.postgrest["categories"]
            .select()
            .decodeList<Category>()
        emit(categories)
    }

    override fun getCategoryById(id: String): Category? {
        // Note: Supabase calls are usually suspend. Since the interface 
        // method is not suspend, we return null for now to keep the code compiling.
        return null
    }
}
