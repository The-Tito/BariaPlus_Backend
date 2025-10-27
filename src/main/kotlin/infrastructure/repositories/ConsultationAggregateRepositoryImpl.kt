package infrastructure.repositories

import domain.interfaces.ConsultationAggregateInterface
import domain.models.ConsultationAggregate
import domain.models.ConsultationComplete
import domain.models.HealthIndicatorWithType
import domain.models.MedicalConsultation
import domain.models.MetricValueWithCatalog
import domain.models.NoteWithCategory
import domain.models.Review
import infrastructure.database.DatabaseFactory.dbQuery
import infrastructure.database.tables.CategoriesTable
import infrastructure.database.tables.CategoryMetricTable
import infrastructure.database.tables.EnergeticExpenditureTable
import infrastructure.database.tables.HealthIndicatorsTable
import infrastructure.database.tables.MeasurementUnitsTable
import infrastructure.database.tables.MedicalConsultationsTable
import infrastructure.database.tables.MetricsCatalogTable
import infrastructure.database.tables.MetricsValueTable
import infrastructure.database.tables.NotesTable
import infrastructure.database.tables.ReviewsTable
import infrastructure.database.tables.TypeIndicatorsTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ConsultationAggregateRepositoryImpl: ConsultationAggregateInterface {
    override suspend fun saveConsultationWithDetails(aggregate: ConsultationAggregate): ConsultationAggregate = dbQuery {
        transaction {
            try {

                val consultationId = MedicalConsultationsTable.insert {
                    it[date] = aggregate.consultation.date
                    it[reason] = aggregate.consultation.reason
                    it[medicalRecordId] = aggregate.consultation.medicalRecordId
                } get MedicalConsultationsTable.id
                val savedConsultation = aggregate.consultation.copy(id = consultationId)

                val savedNotes = aggregate.notes.map { note ->
                    val noteId = NotesTable.insert {
                        it[description] = note.description
                        it[medicalConsultationId] = consultationId
                        it[categoryId] = note.categoryId
                    } get NotesTable.id

                    note.copy(id = noteId, medicalConsultationId = consultationId)
                }



                val savedMetricValues = aggregate.metricsValue.map { metric ->
                    val metricId = MetricsValueTable.insert {
                        it[metricsCatalogId] = metric.metricsCatalogId
                        it[value] = metric.value
                        it[medicalConsultationId] = consultationId
                    } get MetricsValueTable.id

                    metric.copy(id = metricId, medicalConsultationId = consultationId)
                }

                val savedHealthIndicators = aggregate.healthIndicators.map { indicators ->
                    val healthIndicatorId = HealthIndicatorsTable.insert {
                        it[value] = indicators.value
                        it[typeIndicatorId] = indicators.typeIndicatorId
                        it[medicalConsultationId] = consultationId
                    }get HealthIndicatorsTable.id

                    indicators.copy(id = healthIndicatorId, typeIndicatorId = consultationId)
                }

                val savedEnergeticExpenditure = aggregate.energeticExpenditure?.let { energetic ->
                    EnergeticExpenditureTable.insert {
                        it[value] = energetic.energyExpenditure.toBigDecimal()
                        it[medicalConsultationId] = consultationId
                        it[physicalActivityId] = energetic.physicalActivityId
                        it[reductionPercentage] = energetic.reductionPercentage.toBigDecimal()
                        it[adjustedValue] = energetic.energyExpenditureReduction.toBigDecimal()
                    }

                    energetic.copy()

                }

                ConsultationAggregate(
                    consultation = savedConsultation,
                    notes = savedNotes,
                    metricsValue = savedMetricValues,
                    healthIndicators = savedHealthIndicators,
                    energeticExpenditure = savedEnergeticExpenditure
                )
            }catch (e: Exception){
                throw e
            }
        }
    }

    override suspend fun findCompleteConsultation(consultationId: Int): ConsultationComplete? = dbQuery {
        val consultation = MedicalConsultationsTable
            .select { MedicalConsultationsTable.id eq consultationId }
            .map {
                MedicalConsultation(
                    id = it[MedicalConsultationsTable.id],
                    date = it[MedicalConsultationsTable.date],
                    reason = it[MedicalConsultationsTable.reason],
                    medicalRecordId = it[MedicalConsultationsTable.medicalRecordId],
                )
            }
            .singleOrNull() ?: return@dbQuery null

        val notes = (NotesTable innerJoin CategoriesTable)
            .select { NotesTable.medicalConsultationId eq consultationId }
            .map {
                NoteWithCategory(
                    id = it[NotesTable.id],
                    description = it[NotesTable.description],
                    categoryId = it[NotesTable.categoryId],
                    categoryName = it[CategoriesTable.name],
                )
            }



        val metricsValue = (MetricsValueTable innerJoin MetricsCatalogTable innerJoin MeasurementUnitsTable innerJoin CategoryMetricTable)
            .select { MetricsValueTable.medicalConsultationId eq consultationId }
            .map {
                MetricValueWithCatalog(
                    id = it[MetricsCatalogTable.id],
                    value = it[MetricsValueTable.value],
                    metricsId = it[MetricsValueTable.metricsCatalogId],
                    metricName = it[MetricsCatalogTable.name],
                    measurementUnit = it[MeasurementUnitsTable.name],
                    categoryName = it[CategoryMetricTable.name],
                )
            }

        val review = ReviewsTable
            .select { ReviewsTable.medicalConsultationId eq consultationId }
            .map {
                Review(
                    id = it[ReviewsTable.id],
                    puntuation = it[ReviewsTable.puntuation],
                    comments = it[ReviewsTable.comments],
                    date = it[ReviewsTable.date],
                    medicalConsultationId = it[ReviewsTable.medicalConsultationId]
                )
            }
            .singleOrNull()

        ConsultationComplete(
            consultation = consultation,
            notes = notes,
            metricValues = metricsValue,
            review = review,
        )
    }
}