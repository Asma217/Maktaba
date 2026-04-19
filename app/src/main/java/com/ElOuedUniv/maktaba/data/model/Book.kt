package com.ElOuedUniv.maktaba.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String? = null,
    val title: String,
    val author: String = "",
    val imageUrl: String = "",
    val categoryId: String = "",
    val isbn: String = "",
    val nbPages: Int = 0
)