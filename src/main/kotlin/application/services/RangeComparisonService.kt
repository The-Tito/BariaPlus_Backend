package application.services

import domain.interfaces.RangeDetailInterface
import domain.models.HealthIndicatorComparison
import domain.models.RangeDetail
import java.math.BigDecimal

class RangeComparisonService(
    val rangeDetailInterface: RangeDetailInterface
) {

    suspend fun compareIndicatorWithRanges(
        value: BigDecimal,
        typeIndicatorId: Int,
        genderId: Int,
        age: Int
    ): HealthIndicatorComparison {

        val applicableRanges = rangeDetailInterface.findByTypeIndicator(typeIndicatorId)

        if (applicableRanges.isEmpty()) {
            return HealthIndicatorComparison(
                rangeId = 0,
                rangeName = "Sin clasificación"
            )
        }

        // 2. Filtrar rangos que cumplan con género y edad
        val matchingRange = applicableRanges.firstOrNull { range ->
            matchesGender(range, genderId) &&
                    matchesAge(range, age) &&
                    matchesValue(range, value)
        }

        return if (matchingRange != null) {
            HealthIndicatorComparison(
                rangeId = matchingRange.rangeId,
                rangeName = matchingRange.rangeName
            )
        }else {
            HealthIndicatorComparison(
                rangeId = 0,
                rangeName = "Sin clasificación"
            )
        }
    }

    private fun matchesGender(range: RangeDetail, patientGenderId: Int): Boolean {
        return range.genderId == null || range.genderId == patientGenderId
    }

    /**
     * Verifica si la edad del paciente está dentro del rango de edad
     * - Si min_age es NULL: sin límite inferior
     * - Si max_age es NULL: sin límite superior
     */
    private fun matchesAge(range: RangeDetail, patientAge: Int): Boolean {
        val meetsMinAge = range.minAge == null || patientAge >= range.minAge
        val meetsMaxAge = range.maxAge == null || patientAge <= range.maxAge
        return meetsMinAge && meetsMaxAge
    }

    /**
     * Verifica si el valor del indicador está dentro del rango de valores
     * - Si min_value es NULL: sin límite inferior (< max_value)
     * - Si max_value es NULL: sin límite superior (>= min_value)
     */
    private fun matchesValue(range: RangeDetail, value: BigDecimal): Boolean {
        val meetsMinValue = range.minValue == null ||
                value.compareTo(range.minValue) >= 0
        val meetsMaxValue = range.maxValue == null ||
                value.compareTo(range.maxValue) <= 0
        return meetsMinValue && meetsMaxValue
    }

    /**
     * Compara todos los health indicators de una consulta
     */
    suspend fun compareAllIndicators(
        indicators: List<Pair<Int, BigDecimal>>,
        genderId: Int,
        age: Int
    ): List<HealthIndicatorComparison> {
        return indicators.map { (typeIndicatorId, value) ->
            compareIndicatorWithRanges(value, typeIndicatorId, genderId, age)
        }
    }

}