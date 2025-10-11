package domain.models

import java.math.BigDecimal
import java.time.LocalDate

/*Tablas de apoyo*/
data class Categories(
    val id: Int,
    val name: String
)

data class TypeIndicators(
    val id: Int,
    val name: String,
    val measurementUnitId: Int,
    val rangeId: Int,
)

data class MetricsCatalog(
    val id: Int,
    val name: String,
    val measurementUnitId: Int,
    val metricCategoryId: Int
)
/*Tablas principales*/
data class MedicalConsultation(
    val id: Int? = null,
    val date: LocalDate,
    val reason: String,
    val medicalRecordId: Int
    )

data class Note(
    val id: Int? = null,
    val description: String,
    val medicalConsultationId: Int? = null,
    val categoryId: Int
)

data class HealthIndicators(
    val id: Int? = null,
    val value: BigDecimal,
    val typeIndicatorId: Int,
    val medicalConsultationId: Int? = null,
)

data class MetricValue(
    val id: Int? = null,
    val metricsCatalogId: Int,
    val value: BigDecimal,
    val medicalConsultationId: Int? = null
)

data class Review(
    val id: Int? = null,
    val puntuation: Int,
    val comments: String,
    val date: LocalDate,
    val medicalConsultationId: Int? = null,
)

data class ConsultationAggregate(
    val consultation: MedicalConsultation,
    val notes: List<Note> = emptyList(),
    val healthIndicators: List<HealthIndicators> = emptyList(),
    val metricValue: List<MetricValue> = emptyList(),
)

data class ConsultationComplete(
    val consultation: MedicalConsultation,
    val notes: List<NoteWithCategory>,
    val healthIndicators: List<HealthIndicatorWithType>,
    val metricValues: List<MetricValueWithCatalog>,
    val review: Review? = null
)

/**
* DTOs para respuestas enriquecidas
*/

data class NoteWithCategory(
    val id: Int,
    val description: String,
    val categoryId: Int,
    val categoryName: String,
)

data class HealthIndicatorWithType(
    val id: Int,
    val value: BigDecimal,
    val typeIndicatorId: Int,
    val typeName: String,
    val mesurementUnit: String
)

data class MetricValueWithCatalog(
    val id: Int,
    val value: BigDecimal,
    val metricsId: Int,
    val metricName: String,
    val mesurementUnit: String,
    val categoryName: String,

)