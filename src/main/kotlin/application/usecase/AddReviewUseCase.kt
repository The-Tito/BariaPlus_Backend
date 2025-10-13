package application.usecase

import application.dto.AddReviewRequest
import application.dto.AddReviewResponse
import application.dto.ReviewDTO
import domain.interfaces.ConsultationInterface
import domain.interfaces.ReviewsInterface
import domain.models.Review
import java.time.LocalDate

class AddReviewUseCase(
    private val reviewsInterface: ReviewsInterface,
    private val consultationInterface: ConsultationInterface ,
) {

    suspend fun execute(
        consultationId: Int,
        request: AddReviewRequest
    ): AddReviewResponse {

        val consultation = consultationInterface.findById(consultationId)
            ?: throw IllegalArgumentException("Consulta no entrontrada")

        if (reviewsInterface.existsByConsultationId(consultationId)) {
            return AddReviewResponse(
                success = false,
                message = "Esta consulta ya tiene una review"
            )
        }

        val review = Review(
            puntuation = request.puntuation,
            comments = request.comments,
            date = LocalDate.now(),
            medicalConsultationId = consultationId,
        )

        val savedReview = reviewsInterface.save(review)

        return AddReviewResponse(
            success = true,
            message = "Review agregada exitosamente",
            review = ReviewDTO(
                id = savedReview.id!!,
                puntuation = savedReview.puntuation,
                comments = savedReview.comments,
                date = savedReview.date.toString(),
            )
        )
    }
}