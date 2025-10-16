package domain.models

import java.math.BigDecimal

data class TypeIndicators(
    val id: Int,
    val name: String,
    val measurementUnitId: Int,
    val rangeId: Int,
)

data class HealthIndicators(
    val id: Int? = null,
    val value: BigDecimal,
    val typeIndicatorId: Int,
    val medicalConsultationId: Int? = null,
)

data class HealthIndicatorWithType(
    val id: Int,
    val value: BigDecimal,
    val typeIndicatorId: Int,
    val typeName: String,
    val measurementUnit: String
)