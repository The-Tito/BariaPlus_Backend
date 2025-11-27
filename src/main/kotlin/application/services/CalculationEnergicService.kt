package application.services

import application.dto.EnergeticAdjustmentRequestDTO
import application.dto.EnergeticAdjustmentResponseDTO
import application.dto.EnergeticExpenditureRequestDTO
import application.dto.EnergeticExpenditureResponseDTO
import domain.interfaces.ConsultationAggregateInterface
import domain.models.ConsultationAggregate
import java.math.BigDecimal
import java.math.RoundingMode

class CalculationEnergicService(
    private val consultationAggregateInterface: ConsultationAggregateInterface
) {

    fun calculateEnergic(
        energeticAdjustment: EnergeticAdjustmentRequestDTO? = null,
        energeticExpenditure: EnergeticExpenditureRequestDTO,
        peso: BigDecimal,
        talla: BigDecimal,
        age: Int,
        genderId: Int,
        activityFactor: BigDecimal
    ): EnergeticExpenditureResponseDTO {

        var adjustedValue: BigDecimal = BigDecimal.ZERO

        var reductionPercentage: BigDecimal = BigDecimal.ZERO

        val bmr = calculateBMR(peso, talla, age, genderId)

        val tdee = bmr.multiply(activityFactor).setScale(2, RoundingMode.HALF_UP)

        if (energeticAdjustment != null) {
        reductionPercentage = BigDecimal(energeticAdjustment.adjustmentPercentage)
        adjustedValue = if (reductionPercentage > BigDecimal.ZERO) {
            calculateAdjustedValue(tdee, reductionPercentage)
        } else {
            tdee
        }
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

    suspend fun applyEnergyAdjustment(request: EnergeticAdjustmentRequestDTO): EnergeticAdjustmentResponseDTO {
        val adjustmentPercent = request.adjustmentPercentage.toBigDecimalOrNull()
            ?: return EnergeticAdjustmentResponseDTO(false, "Porcentaje inválido")


        val originalValue = request.energyExpenditure.toBigDecimal()

        val reductionFactor = BigDecimal.ONE.subtract(
            adjustmentPercent.divide(BigDecimal("100"), 4, RoundingMode.HALF_UP)
        )
        val adjustedValue = originalValue.multiply(reductionFactor).setScale(2, RoundingMode.HALF_UP)

        val updateSuccess = consultationAggregateInterface.updateEnergyExpenditure(
            consultationId = request.consultationId,
            adjustmentPercentage = adjustmentPercent,
            adjustedValue = adjustedValue
        )

        return if (updateSuccess.success) {
            EnergeticAdjustmentResponseDTO(true, "Ajuste aplicado correctamente", adjustedValue.toString())
        } else {
            EnergeticAdjustmentResponseDTO(false, "Error al guardar el ajuste")
        }
    }
}


