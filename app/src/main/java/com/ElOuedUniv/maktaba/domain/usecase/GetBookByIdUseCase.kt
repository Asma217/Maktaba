package com.ElOuedUniv.maktaba.domain.usecase

import com.ElOuedUniv.maktaba.data.repository.BookRepository
import javax.inject.Inject

class GetBookByIdUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(id: String) = bookRepository.getBookById(id)
}