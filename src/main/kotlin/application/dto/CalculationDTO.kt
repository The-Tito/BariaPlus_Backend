package application.dto

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class CreateConsultationRequest(
    val patientId: Int,
    val medicalRecordId: Int,
    val reason: String,
    val notes: List<NoteRequestDTO> = emptyList(),
    val metricValues: List<MetricValueRequestDTO> = emptyList(),
    val energeticExpenditure: EnergeticExpenditureRequestDTO

)

@Serializable
data class NoteRequestDTO(
    val description: String,
    val categoryId: Int
)


@Serializable
data class MetricValueRequestDTO(
    val metricsCatalogId: Int,
    val value: String // Decimal as String
)

@Serializable
data class EnergeticExpenditureRequestDTO(
    val physicalActivityId: Int
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
    val calculatedMetrics: List<CalculatedMetricDTO> = emptyList(),
    val originalMetrics: List<MetricsResponseDTO> = emptyList(),
    val originalNotes: List<NoteRequestDTO> = emptyList(),
    val energeticExpenditure: EnergeticExpenditureResponseDTO? = null
)

@Serializable
data class EnergeticExpenditureResponseDTO(
    val energeticExpenditureId: Int? = null,
    val physicalActivityId: Int,
    val energyExpenditure: String,
    val reductionPercentage: String = "",
    val energyExpenditureReduction: String = "",
)

@Serializable
data class CalculatedIndicatorDTO(
    val typeIndicatorId: Int,
    val nameIndicator: String,
    val value: String,
    val rangeId: Int,
    val rangeName: String,
)

@Serializable
data class CalculatedMetricDTO(
    val catalogId: Int,
    val nameCatalog: String,
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

/**
 * Agregar una gasto % de gasto energetico
 */
@Serializable
data class EnergeticAdjustmentRequestDTO(
    val consultationId: Int,
    val energyExpenditure: String,
    val adjustmentPercentage: String
)

@Serializable
data class EnergeticAdjustmentResponseDTO(
    val success: Boolean,
    val message: String,
    val adjustedEnergyExpenditure: String? = null
)
@Serializable
data class EnergeticAdjustmentResponse(
    val consultationId: Int,
    val energyExpenditure: String,
    val adjustmentPercentage: String,
    val adjustedEnergyExpenditure: String? = null

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
data class MetricValueDTO(
    val id: Int,
    val value: String,
    val metricsCatalogId: Int,
    val metricsCatalogName: String,
    val measurementUnitName: String,
    val metricsCategoryName: String
)

@Serializable
data class MetricsResponseDTO(
    val metricsCatalogId: Int,
    val value: String,
)

