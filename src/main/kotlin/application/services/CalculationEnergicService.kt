package application.services

import application.dto.EnergeticExpenditureRequestDTO
import application.dto.EnergeticExpenditureResponseDTO
import java.math.BigDecimal
import java.math.RoundingMode

class CalculationEnergicService {

    fun calculateEnergic(
        energeticExpenditure: EnergeticExpenditureRequestDTO,
        peso: BigDecimal,
        talla: BigDecimal,
        age: Int,
        genderId: Int,
        activityFactor: BigDecimal
    ): EnergeticExpenditureResponseDTO {

        val bmr = calculateBMR(peso, talla, age, genderId)

        val tdee = bmr.multiply(activityFactor).setScale(2, RoundingMode.HALF_UP)

        val reductionPercentage = BigDecimal(energeticExpenditure.reductionPercentage)
        val adjustedValue = if (reductionPercentage > BigDecimal.ZERO) {
            calculateAdjustedValue(tdee, reductionPercentage)
        } else {
            tdee
        }


        return EnergeticExpenditureResponseDTO(
            physicalActivityId = energeticExpenditure.physicalActivityId,
            energyExpenditure = tdee.toString(),
            reductionPercentage = reductionPercentage.toString(),
            energyExpenditureReduction = adjustedValue.toString()
        )
    }

    private fun calculateBMR(
        peso: BigDecimal,
        talla: BigDecimal,
        age: Int,
        genderId: Int
    ): BigDecimal {
        val pesoFactor = BigDecimal("10").multiply(peso)
        val tallaFactor = BigDecimal("6.25").multiply(talla)
        val ageFactor = BigDecimal("5").multiply(BigDecimal(age))

        val bmr = pesoFactor
            .add(tallaFactor)
            .subtract(ageFactor)

        // Ajuste por género
        return if (genderId == 2) { // Masculino
            bmr.add(BigDecimal("5"))
        } else { // Femenino
            bmr.subtract(BigDecimal("161"))
        }.setScale(2, RoundingMode.HALF_UP)
    }

    private fun calculateAdjustedValue(
        tdee: BigDecimal,
        reductionPercentage: BigDecimal
    ): BigDecimal {
        val reductionFactor = BigDecimal.ONE.subtract(
            reductionPercentage.divide(BigDecimal("100"), 4, RoundingMode.HALF_UP)
        )
        return tdee.multiply(reductionFactor).setScale(2, RoundingMode.HALF_UP)
    }


}