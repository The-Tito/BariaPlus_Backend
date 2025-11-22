package application.dto

import kotlinx.serialization.Serializable

@Serializable
data class IndicatorStatsResponse(
    val success: Boolean,
    val message: String,
    val patientId: Int,
    val indicatorId: Int,
    val indicatorName: String,
    val data: List<IndicatorStatsPoint>
)

@Serializable
data class IndicatorStatsPoint(
    val date: String,      // Fecha de la consulta
    val value: Double      // Valor del indicador en esa consulta
)