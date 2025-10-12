package application.dto

import domain.interfaces.HealthIndicatorInterface
import domain.models.HealthIndicators
import domain.models.TypeIndicators
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class CreateConsultationRequest(
    val date: String,
    val reason: String,
    val medicalRecordId: Int,
    val notes: List<NotesRequestDTO> = emptyList(),
    val healthIndicators: List<HealthIndicatorsRequestDTO> = emptyList(),
    val metricsValue: List<MetricsValueRequestDTO> = emptyList(),
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
data class ConsultationInfoDTO(
    val id: Int,
    val date: String,
    val reason: String,
    val medicalRecordId: Int,

)

@Serializable
data class ReviewsRequestDTO(
    val puntuation: Int,
    val comments: String,
)