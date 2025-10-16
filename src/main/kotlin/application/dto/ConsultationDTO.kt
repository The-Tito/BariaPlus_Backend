package application.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateConsultationRequest(
    val date: String,
    val reason: String,
    val medicalRecordId: Int,
    val metricValues: List<MetricsValueRequestDTO> = emptyList(),
    val notes: List<NotesRequestDTO> = emptyList(),
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
    val metricsCount: Int,

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
    val metricsCatalog: List<MetricsCatalogDTO>,
)

@Serializable
data class CategoryDTO(
    val id: Int,
    val name: String,
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