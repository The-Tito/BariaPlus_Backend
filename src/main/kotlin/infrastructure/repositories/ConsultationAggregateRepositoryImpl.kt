package infrastructure.repositories

import application.dto.CalculatedIndicatorDTO
import application.dto.CalculatedIndicatorResponse
import application.dto.CalculatedMetrics
import application.dto.ConsultationCompleteResponseDTO
import application.dto.EnergeticAdjustmentResponseDTO
import application.dto.EnergeticExpenditureResponse
import application.dto.HealthIndicatorsAux
import application.dto.MedicalConsultationResponse
import application.dto.OriginalMetrics
import application.dto.OriginalNotes
import application.dto.PatientInfoAux
import application.services.RangeComparisonService
import domain.interfaces.ConsultationAggregateInterface
import domain.models.ConsultationAggregate
import domain.models.EnergeticExpenditure
import domain.models.HealthIndicatorComparison
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
import infrastructure.database.tables.MedicalRecordsTable
import infrastructure.database.tables.MetricsCatalogTable
import infrastructure.database.tables.MetricsValueTable
import infrastructure.database.tables.NotesTable
import infrastructure.database.tables.PatientsTable
import infrastructure.database.tables.ReviewsTable
import infrastructure.database.tables.TypeIndicatorsTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period

class ConsultationAggregateRepositoryImpl(
    override val rangeComparisonService: RangeComparisonService
) : ConsultationAggregateInterface {
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
                    val savedEnergeticExpenditureId = EnergeticExpenditureTable.insert {
                        it[value] = energetic.energyExpenditure.toBigDecimal()
                        it[medicalConsultationId] = consultationId
                        it[physicalActivityId] = energetic.physicalActivityId
                        it[reductionPercentage] = energetic.reductionPercentage.toBigDecimal()
                        it[adjustedValue] = energetic.energyExpenditureReduction.toBigDecimal()
                    }get EnergeticExpenditureTable.id

                    energetic.copy(energeticExpenditureId = savedEnergeticExpenditureId)

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

    override suspend fun findCompleteConsultation(consultationId: Int): ConsultationCompleteResponseDTO? = dbQuery {
        try {
            val consultation = (MedicalConsultationsTable
                .join(MedicalRecordsTable, JoinType.INNER,
                    MedicalConsultationsTable.medicalRecordId, MedicalRecordsTable.id)
                .join(PatientsTable, JoinType.INNER,
                    MedicalRecordsTable.patientId, PatientsTable.id))  // ← FK explícita
                .select { MedicalConsultationsTable.id eq consultationId }
                .map {
                    MedicalConsultationResponse(
                        id = it[MedicalConsultationsTable.id],
                        date = it[MedicalConsultationsTable.date].toString(),
                        reason = it[MedicalConsultationsTable.reason],
                        medicalRecordId = it[MedicalConsultationsTable.medicalRecordId],
                        genderId = it[PatientsTable.genderId],
                        firstName = it[PatientsTable.firstName],
                        lastName = it[PatientsTable.lastName]
                    )
                }.singleOrNull() ?: return@dbQuery null

            val healthIndicators = (HealthIndicatorsTable innerJoin TypeIndicatorsTable)
                .select {  HealthIndicatorsTable.medicalConsultationId eq consultationId }
                .map {
                    HealthIndicatorsAux(
                        typeIndicatorId = it[HealthIndicatorsTable.typeIndicatorId],
                        nameIndicator =  it[TypeIndicatorsTable.name],
                        value = it[HealthIndicatorsTable.value],
                    )
                }

            val patientInfo = (MedicalRecordsTable innerJoin PatientsTable)
                .select { MedicalRecordsTable.id eq consultation.medicalRecordId }
                .limit(1)
                .map {
                    val dob = it[PatientsTable.dateOfBirth]
                    val age = Period.between(dob, LocalDate.now()).years
                    PatientInfoAux(
                        genderId = it[PatientsTable.genderId],
                        age = age
                    )
                }
                .first()


            val indicatorComparisons = compareHealthIndicatorsWithRanges(
                healthIndicators,
                patientInfo
            )

            val calculatedIndicators = healthIndicators.mapIndexed { index, indicator ->
                val comparison = indicatorComparisons.getOrNull(index)
                CalculatedIndicatorResponse(
                    typeIndicatorId = indicator.typeIndicatorId,
                    nameIndicator = indicator.nameIndicator,
                    value = indicator.value.toString(),
                    rangeId = comparison?.rangeId ?: 0,              // usar valores seguros o por defecto
                    rangeName = comparison?.rangeName ?: "Sin rango"
                )
            }
            val allowedCatalogIds = listOf(3, 4, 5, 21)

            val calculatedMetrics = (MetricsValueTable innerJoin MetricsCatalogTable)
                .select {
                    (MetricsValueTable.medicalConsultationId eq consultationId) and
                            (MetricsValueTable.metricsCatalogId inList allowedCatalogIds)
                }
                .map {
                    CalculatedMetrics(
                        catalogId = it[MetricsValueTable.metricsCatalogId],
                        nameCatalog = it[MetricsCatalogTable.name],
                        value = it[MetricsValueTable.value].toString()
                    )
                }

            val excludedCatalogIds = listOf(3, 4, 5, 21)

            val originalMetrics = (MetricsValueTable innerJoin MetricsCatalogTable)
                .select {
                    (MetricsValueTable.medicalConsultationId eq consultationId) and
                            (MetricsValueTable.metricsCatalogId notInList excludedCatalogIds)
                }
                .map {
                    OriginalMetrics(
                        metricsCatalogId = it[MetricsValueTable.metricsCatalogId],
                        nameCatalog = it[MetricsCatalogTable.name],
                        value = it[MetricsValueTable.value].toString()
                    )
                }

            val originalNotes = (NotesTable)
                .select { NotesTable.medicalConsultationId eq consultationId }
                .map {
                    OriginalNotes(
                        description = it[NotesTable.description],
                        categoryId = it[NotesTable.categoryId],
                    )
                }

            val energeticExpenditure = EnergeticExpenditureTable
                .select { EnergeticExpenditureTable.medicalConsultationId eq consultationId }
                .limit(1)
                .map {
                    EnergeticExpenditureResponse(
                        physicalActivityId = it[EnergeticExpenditureTable.physicalActivityId],
                        energyExpenditure = it[EnergeticExpenditureTable.value].toString(),
                        reductionPercentage = it[EnergeticExpenditureTable.reductionPercentage].toString(),
                        energyExpenditureReduction = it[EnergeticExpenditureTable.adjustedValue].toString(),
                    )
                }.first()




            ConsultationCompleteResponseDTO(
                success = true,
                message = "Consulta encontrada con exito",
                consultation = consultation,
                calculatedIndicators = calculatedIndicators,
                calculatedMetrics = calculatedMetrics,
                originalMetrics = originalMetrics,
                originalNotes = originalNotes,
                energeticExpenditure = energeticExpenditure
            )

        }catch (e:Exception){
            ConsultationCompleteResponseDTO(
                success = false,
                message = "Error al obtener consulta: ${e.message}",
                consultation = null
            )

        }

    }

    override suspend fun updateEnergyExpenditure(
        consultationId: Int,
        adjustmentPercentage: BigDecimal,
        adjustedValue: BigDecimal
    ): EnergeticAdjustmentResponseDTO = dbQuery {
        try {
            EnergeticExpenditureTable.update({ EnergeticExpenditureTable.medicalConsultationId eq consultationId}){
                it[reductionPercentage] = adjustmentPercentage
                it[this.adjustedValue] = adjustedValue
            }
            EnergeticAdjustmentResponseDTO(
                success = true,
                message = "Se actualizo correctamente",
                adjustedEnergyExpenditure = adjustedValue.toString()
            )
        }catch (e: Exception){
            EnergeticAdjustmentResponseDTO(
                success = false,
                message = "No se pudo registrar el ajuste",
                adjustedEnergyExpenditure = null
            )
        }
    }

private suspend fun compareHealthIndicatorsWithRanges(
    healthIndicators: List<HealthIndicatorsAux>,
    patientInfo: PatientInfoAux,
): List<HealthIndicatorComparison> {
    return healthIndicators.map { indicator ->
        rangeComparisonService.compareIndicatorWithRanges(
            value = indicator.value,
            typeIndicatorId = indicator.typeIndicatorId,
            genderId = patientInfo.genderId,
            age = patientInfo.age
        )
    }
}

}
