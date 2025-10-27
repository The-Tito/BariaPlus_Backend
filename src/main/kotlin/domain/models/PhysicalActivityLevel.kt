package domain.models

import java.math.BigDecimal

data class PhysicalActivityLevel(
    val id: Int? = null,
    val name: String,
    val description: String?,
    val activityFactor: BigDecimal
)
