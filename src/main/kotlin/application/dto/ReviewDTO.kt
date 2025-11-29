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


@Serializable
data class GetReviewsResponse(
    val success: Boolean,
    val message: String,
    val average: String,
    val reviews: List<ReviewResponse> = emptyList()
)

@Serializable
data class ReviewResponse(
    val id: Int,
    val name: String,
    val comments: String,
    val date: String,
    val puntuation: Int
)