package com.ElOuedUniv.maktaba.data.di

import com.ElOuedUniv.maktaba.data.repository.BookRepository
import com.ElOuedUniv.maktaba.data.repository.BookRepositoryImpl
import com.ElOuedUniv.maktaba.data.repository.CategoryRepository
import com.ElOuedUniv.maktaba.data.repository.CategoryRepositoryImpl
import com.ElOuedUniv.maktaba.data.repository.SupabaseBookRepositoryImpl
import com.ElOuedUniv.maktaba.data.repository.SupabaseCategoryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideBookRepository(client: SupabaseClient): BookRepository {
        return SupabaseBookRepositoryImpl(client)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(client: SupabaseClient): CategoryRepository {
        return SupabaseCategoryRepositoryImpl(client)
    }
}
