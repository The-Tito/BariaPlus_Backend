package domain.interfaces

import application.dto.ConsultationCompleteResponseDTO
import application.dto.EnergeticAdjustmentResponseDTO
import application.services.RangeComparisonService
import domain.models.Categories
import domain.models.ConsultationAggregate
import domain.models.HealthIndicatorWithType
import domain.models.HealthIndicators
import domain.models.MedicalConsultation
import domain.models.MetricsValue
import domain.models.MetricValueWithCatalog
import domain.models.MetricsCatalog
import domain.models.NoteWithCategory
import domain.models.Notes
import domain.models.Review
import domain.models.TypeIndicators
import java.math.BigDecimal


interface ConsultationInterface {
    suspend fun save(consultation: MedicalConsultation): MedicalConsultation
    suspend fun findById(id: Int): MedicalConsultation?
    suspend fun findByMedicalRecordId(medicalRecordId: Int): List<MedicalConsultation>
}

interface NotesInterface {
    suspend fun save(notes: List<Notes>): List<Notes>
    suspend fun findByConsultationId(consultationId: Int): List<NoteWithCategory>

}

interface HealthIndicatorInterface {
    suspend fun save(indicators: List<HealthIndicators>): List<HealthIndicators>
    suspend fun findById(consultationId: Int): List<HealthIndicatorWithType>
}

interface MetricsValue{
    suspend fun save(metrics: List<MetricsValue>): List<MetricsValue>
    suspend fun findById(metricId: Int): List<MetricValueWithCatalog>
}

interface ReviewsInterface {
    suspend fun save(review: Review): Review
    suspend fun findById(consultationId: Int): Review?
    suspend fun existsByConsultationId(consultationId: Int): Boolean
}

interface CatalogInterface {
    suspend fun findAllCategories(): List<Categories>
    suspend fun findAllTypeIndicators(): List<TypeIndicators>
    suspend fun findAllMetricsCatalog(): List<MetricsCatalog>
}


/**
 * Repositorio agregado para operaciones transaccionales de consultas
 */

interface ConsultationAggregateInterface {
    val rangeComparisonService: RangeComparisonService

    suspend fun saveConsultationWithDetails(aggregate: ConsultationAggregate): ConsultationAggregate
    suspend fun findCompleteConsultation(consultationId: Int): ConsultationCompleteResponseDTO?
    suspend fun updateEnergyExpenditure(consultationId: Int, adjustmentPercentage: BigDecimal, adjustedValue: BigDecimal): EnergeticAdjustmentResponseDTO
}
