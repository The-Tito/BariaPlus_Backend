package application.dto

import domain.models.MedicalConsultation
import kotlinx.serialization.Serializable
import java.math.BigDecimal


@Serializable
data class ConsultationCompleteResponseDTO(
    val success: Boolean,
    val message: String,
    val consultation: MedicalConsultationResponse?,
    val calculatedIndicators: List<CalculatedIndicatorResponse> = emptyList(),
    val calculatedMetrics: List<CalculatedMetrics> = emptyList(),
    val originalMetrics: List<OriginalMetrics> = emptyList(),
    val originalNotes: List<OriginalNotes> = emptyList(),
    val energeticExpenditure: EnergeticExpenditureResponse? = null,
)

@Serializable
data class MedicalConsultationResponse(
    val id: Int? = null,
    val date: String,
    val reason: String,
    val medicalRecordId: Int
)

@Serializable
data class CalculatedIndicatorResponse(
    val typeIndicatorId: Int,
    val nameIndicator: String,
    val value: String,
    val rangeId: Int,
    val rangeName: String,
)

@Serializable
data class CalculatedMetrics(
    val catalogId: Int,
    val nameCatalog: String,
    val value: String,
)

@Serializable
data class OriginalMetrics(
    val metricsCatalogId: Int,
    val nameCatalog: String,
    val value: String,
)

@Serializable
data class OriginalNotes(
    val description: String,
    val categoryId: Int
)

@Serializable
data class EnergeticExpenditureResponse(
    val physicalActivityId: Int,
    val energyExpenditure: String,
    val reductionPercentage: String,
    val energyExpenditureReduction: String,
)


data class HealthIndicatorsAux(
    val typeIndicatorId: Int,
    val nameIndicator: String,
    val value: BigDecimal,
)

data class PatientInfoAux(
    val genderId: Int,
    val age: Int
)