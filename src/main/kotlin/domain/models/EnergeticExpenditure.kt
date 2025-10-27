package domain.models

import java.math.BigDecimal

data class EnergeticExpenditure(
    val id: Int? = null,
    val medicalConsultationId: Int? = null,
    val physicalActivityId: Int,
    val value: BigDecimal,
    val reductionPercentage: BigDecimal?,     // % de reducción
    val adjustedValue: BigDecimal?            // TDEE con reducción aplicada
)