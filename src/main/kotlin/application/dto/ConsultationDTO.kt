package application.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateConsultationRequest(
    val date: String,
    val reason: String,
    val medicalRecordId: Int,
    val notes: List<NotesRequestDTO> = emptyList(),
    val healthIndicators: List<HealthIndicatorsRequestDTO> = emptyList(),
    val metricValues: List<MetricsValueRequestDTO> = emptyList(),
    )

@Serializable
data class HealthIndicatorsRequestDTO(
    val typeIndicatorId: Int,
    val value: String,
)

@Serializable
data class MetricsValueRequestDTO(
    val metricsCatalogId: Int,
    val value: String,
)

@Serializable
data class NotesRequestDTO(
    val description: String,
    val categoryId: Int,
)

/**
 * Respuesta al crear consulta
 */

@Serializable
data class CreateConsultationResponse(
    val success: Boolean,
    val message: String,
    val consultation: ConsultationInfoDTO? = null,
)

@Serializable
data class ConsultationInfoDTO(
    val id: Int,
    val date: String,
    val reason: String,
    val medicalRecordId: Int,
    val notesCount:Int,
    val healthIndicatorsCount: Int,
    val metricsCount: Int,

)

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

/**
 * Respuesta completa de consulta con todos sus detalles, esto es un get ya con los datos procesados
 */
@Serializable
data class ConsultationDetailResponse(
    val success: Boolean,
    val consultation: ConsultationDetailDTO
)


@Serializable
data class ConsultationDetailDTO(
    val id: Int,
    val date: String,
    val reason: String,
    val medicalRecordId: Int,
    val notes: List<NotesResponseDTO>,
    val healthIndicators: List<HealthIndicatorsResponseDTO> = emptyList(),
    val metricsValue: List<MetricsValueResponseDTO> = emptyList(),
)


@Serializable
data class NotesResponseDTO(
    val id: Int,
    val description: String,
    val categoryId: Int,
    val categoryName: String,
)

@Serializable
data class HealthIndicatorsResponseDTO(
    val id: Int,
    val value: String,
    val typeIndicatorId: Int,
    val typeIndicatorName: String,
    val measurementUnit: String,
)

@Serializable
data class MetricsValueResponseDTO(
    val id: Int,
    val value: String,
    val metricsCatalogId: Int,
    val metricsCatalogName: String,
    val measurementUnitName: String,
    val metricsCategoryName: String
)

/**
 * Respuesta con catálogos disponibles
 */

data class CatalogResponseDTO(
    val success: Boolean,
    val noteCategories: List<CategoryDTO>,
    val typeIndicators: List<TypeIndicatorDTO>,
    val metricsCatalog: List<MetricsCatalogDTO>,
)

@Serializable
data class CategoryDTO(
    val id: Int,
    val name: String,
)

@Serializable
data class TypeIndicatorDTO(
    val id: Int,
    val name: String,
    val measurementUnitId: Int,
    val measurementUnitName: String
)

@Serializable
data class MetricsCatalogDTO(
    val id: Int,
    val name: String,
    val measurementUnitId: Int,
    val measurementUnitName: String,
    val categoryId: Int,
    val categoryName: String,
)