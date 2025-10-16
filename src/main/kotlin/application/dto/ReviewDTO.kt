package application.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddReviewRequest(
    val puntuation: Int,
    val comments: String,
)

@Serializable
data class AddReviewResponse(
    val success: Boolean,
    val message: String,
    val review: ReviewDTO? = null
)

@Serializable
data class ReviewDTO(
    val id: Int,
    val puntuation: Int,
    val comments: String,
    val date: String
)