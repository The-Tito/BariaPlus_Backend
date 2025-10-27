package domain.models

import java.math.BigDecimal

data class HealthIndicatorComparison(
    val rangeId: Int,
    val rangeName: String
)


data class RangeDetail(
    val id: Int? = null,
    val rangeId: Int,
    val rangeName: String,              // Join con tabla ranges
    val genderId: Int?,
    val minAge: Int?,
    val maxAge: Int?,
    val minValue: BigDecimal?,
    val maxValue: BigDecimal?,
    val typeIndicatorId: Int
)