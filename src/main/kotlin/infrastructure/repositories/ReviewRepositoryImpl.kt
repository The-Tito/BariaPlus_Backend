package infrastructure.repositories

import domain.interfaces.ReviewsInterface
import domain.models.Review
import infrastructure.database.DatabaseFactory.dbQuery
import infrastructure.database.tables.ReviewsTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class ReviewRepositoryImpl : ReviewsInterface {

    override suspend fun save(review: Review): Review = dbQuery {
        val id = ReviewsTable.insert {
            it[puntuation] = review.puntuation
            it[comments] = review.comments
            it[date] = review.date
            it[medicalConsultationId] = review.medicalConsultationId
        } get ReviewsTable.id

        review.copy(id = id)
    }

    override suspend fun findById(consultationId: Int): Review? = dbQuery {
        ReviewsTable
            .select { ReviewsTable.medicalConsultationId eq consultationId }
            .map { resultRowToReview(it) }
            .singleOrNull()
    }

    override suspend fun existsByConsultationId(consultationId: Int): Boolean = dbQuery {
        ReviewsTable
            .select { ReviewsTable.medicalConsultationId eq consultationId }
            .count() > 0
    }

    private fun resultRowToReview(row: ResultRow) = Review(
        id = row[ReviewsTable.id],
        puntuation = row[ReviewsTable.puntuation],
        comments = row[ReviewsTable.comments],
        date = row[ReviewsTable.date],
        medicalConsultationId = row[ReviewsTable.medicalConsultationId]
    )
}
