package application.dto

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class CreateConsultationRequest(
    val patientId: Int,
    val medicalRecordId: Int,
    val reason: String,
    val notes: List<NoteRequestDTO> = emptyList(),
    val metricValues: List<MetricValueRequestDTO> = emptyList()
)

@Serializable
data class NoteRequestDTO(
    val description: String,
    val categoryId: Int
)

@Serializable
data class HealthIndicatorRequestDTO(
    val typeIndicatorId: Int,
    val value: String // Decimal as String para evitar problemas de serialización
)

@Serializable
data class MetricValueRequestDTO(
    val metricsCatalogId: Int,
    val value: String // Decimal as String
)

/**
 * Respuesta al crear consulta
 */
@Serializable
data class CreateConsultationResponse(
    val success: Boolean,
    val message: String,
    val consultation: ConsultationInfoDTO? = null,
    val calculatedIndicators: List<CalculatedIndicatorDTO> = emptyList(),
    val calculatedMetrics: List<CalculatedMetricDTO> = emptyList()
)

@Serializable
data class CalculatedIndicatorDTO(
    val typeIndicatorId: Int,
    val value: String,
    val rangeId: Int,
    val rangeName: String,
    val color: String
)

@Serializable
data class CalculatedMetricDTO(
    val catalogId: Int,
    val value: String
)

@Serializable
data class ConsultationInfoDTO(
    val id: Int,
    val date: String,
    val reason: String,
    val medicalRecordId: Int,
    val notesCount: Int,
    val healthIndicatorsCount: Int,
    val metricsCount: Int
)

@Serializable
data class HealthIndicatorDTO(
    val id: Int,
    val value: String,
    val typeId: Int,
    val typeName: String,
    val measurementUnit: String,
    val rangeId: Int,
    val rangeName: String,
    val color: String
)



/**
 * Respuesta completa de consulta con todos sus detalles
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
    val notes: List<NoteDTO>,
    val metricValues: List<MetricValueDTO>,
)

@Serializable
data class NoteDTO(
    val id: Int,
    val description: String,
    val categoryId: Int,
    val categoryName: String
)

@Serializable
data class HealthIndicatorSDTO(
    val id: Int,
    val value: String,
    val typeId: Int,
    val typeName: String,
    val measurementUnit: String
)

@Serializable
data class MetricValueDTO(
    val id: Int,
    val value: String,
    val metricsCatalogId: Int,
    val metricsCatalogName: String,
    val measurementUnitName: String,
    val metricsCategoryName: String
)

