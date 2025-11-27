package domain.models

import application.dto.EnergeticExpenditureResponseDTO
import java.math.BigDecimal
import java.time.LocalDate

/*Tablas de apoyo*/
data class Categories(
    val id: Int,
    val name: String
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

data class Notes(
    val id: Int? = null,
    val description: String,
    val medicalConsultationId: Int? = null,
    val categoryId: Int
)

data class MetricsValue(
    val id: Int? = null,
    val metricsCatalogId: Int,
    val value: BigDecimal,
    val medicalConsultationId: Int? = null,
)

data class Review(
    val id: Int? = null,
    val puntuation: Int,
    val comments: String,
    val date: LocalDate,
    val medicalConsultationId: Int,
)

data class ConsultationAggregate(
    val consultation: MedicalConsultation,
    val notes: List<Notes> = emptyList(),
    val metricsValue: List<MetricsValue> = emptyList(),
    val healthIndicators: List<HealthIndicators> = emptyList(),
    val energeticExpenditure: EnergeticExpenditureResponseDTO? = null,
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



data class MetricValueWithCatalog(
    val id: Int,
    val value: BigDecimal,
    val metricsId: Int,
    val metricName: String,
    val measurementUnit: String,
    val categoryName: String,

    )