package com.ElOuedUniv.maktaba.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String? = null,
    val title: String,
    val author: String,
    @SerialName("imageUrl") val imageUrl: String? = null,
    @SerialName("categoryId") val categoryId: String? = null,
    val isbn: String,
    @SerialName("nbPages") val nbPages: Int,
    @SerialName("pdfUrl") val pdfUrl: String? = null
)