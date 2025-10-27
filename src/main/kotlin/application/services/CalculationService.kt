package application.services

import domain.models.CalculationCatalog
import domain.models.CalculationIndicatorsResult
import domain.models.CalculationInput
import domain.models.CompleteCalculationResult
import domain.models.HealthStatus
import domain.models.MetricCatalogIds
import domain.models.TypeIndicatorIds
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.log10

class CalculationService {


    fun calculateAll(input: CalculationInput): CompleteCalculationResult {
        val healthIndicators = mutableListOf<CalculationIndicatorsResult>()
        val calculatedMetrics = mutableListOf<CalculationCatalog>()

        // Variables para cálculos intermedios
        var pesoLorentz: BigDecimal? = null
        var pesoBrocca: BigDecimal? = null
        var pesoPromedio: BigDecimal? = null
        var sumaPliegues: BigDecimal? = null
        var kgMasaMuscular: BigDecimal? = null

        // 1. CALCULAR MÉTRICAS DE CATÁLOGO (que van a metrics_value)

        // Peso Brocca
        if (input.talla != null) {
            pesoBrocca = calculatePesoBrocca(input.talla)
            calculatedMetrics.add(
                CalculationCatalog(
                    catalogId = MetricCatalogIds.PESO_BROCCA,
                    nameCatalog = "Peso Brocca",
                    value = pesoBrocca
                )
            )
        }

        // Peso Lorentz
        if (input.talla != null) {
            pesoLorentz = calculatePesoLorentz(input.talla, input.genderId)
            calculatedMetrics.add(
                CalculationCatalog(
                    catalogId = MetricCatalogIds.PESO_LORENTZ,
                    nameCatalog = "Peso Lorentz",
                    value = pesoLorentz
                )
            )
        }

        // Peso Ideal Promedio
        if (input.talla != null) {
            pesoPromedio = calculatePesoIdealPromedio(input.talla, input.genderId)
            calculatedMetrics.add(
                CalculationCatalog(
                    catalogId = MetricCatalogIds.PESO_IDEAL_PROMEDIO,
                    nameCatalog = "Peso Promedio",
                    value = pesoPromedio
                )
            )
        }

        // Suma de Pliegues
        sumaPliegues = calculateSumaPliegues(input)
        if (sumaPliegues > BigDecimal.ZERO) {
            calculatedMetrics.add(
                CalculationCatalog(
                    catalogId = MetricCatalogIds.SUMA_PLIEGUES,
                    nameCatalog = "Suma Pliegues",
                    value = sumaPliegues
                )
            )
        }

        // 2. CALCULAR HEALTH INDICATORS (que van a health_indicators)

        // IMC
        if (input.peso != null && input.talla != null) {
            val imc = calculateIMC(input.peso, input.talla)
            healthIndicators.add(imc)
        }

        // Índice Cintura/Cadera
        if (input.cintura != null && input.cadera != null) {
            val icc = calculateIndiceCinturaCadera(input.cintura, input.cadera)
            healthIndicators.add(icc)
        }

        // Porcentaje de Grasa Corporal (de pliegues si no viene de bioimpedancia)
        if (sumaPliegues != null && sumaPliegues > BigDecimal.ZERO) {
            val grasaCorporal = calculatePorcentajeGrasaCorporal(sumaPliegues)
            healthIndicators.add(grasaCorporal)

            // Grasa Visceral (derivada de grasa corporal)
            val grasaVisceral = calculateGrasaViceral(grasaCorporal)
            healthIndicators.add(grasaVisceral)

            // Kg de Masa Muscular
            if (input.peso != null) {
                kgMasaMuscular = calculateKgMasaMuscular(sumaPliegues, input.peso)

                // Porcentaje de Masa Muscular
                val porcentajeMM = calculatePorcentajeMasaMuscular(input.peso, kgMasaMuscular)
                healthIndicators.add(porcentajeMM)
            }
        } else if (input.porcentajeGrasaCorporal != null) {
            // Si viene de bioimpedancia, solo agregar como indicador
            healthIndicators.add(
                CalculationIndicatorsResult(
                    typeIndicatorId = TypeIndicatorIds.PORCENTAJE_GRASA_CORPORAL,
                    value = input.porcentajeGrasaCorporal,
                    nameIndicator = "Porcentaje Grasa Corporal",
                    status = HealthStatus(0, "")
                )
            )

            // Calcular grasa visceral basada en la de bioimpedancia
            val grasaVisceral = calculateGrasaViceralFromPercentage(input.porcentajeGrasaCorporal)
            healthIndicators.add(grasaVisceral)
        }



        // 3. Crear input completo con valores calculados
        val completeInput = input.copy(
            pesoBrocca = pesoBrocca,
            pesoLorentz = pesoLorentz,
            pesoIdealPrimedio = pesoPromedio,
            sumaPliegues = sumaPliegues
        )

        return CompleteCalculationResult(
            healthIndicators = healthIndicators,
            calculatedMetrics = calculatedMetrics,
            completeInput = completeInput
        )
    }

    // ========== FUNCIONES DE CÁLCULO ==========

    private fun calculateIMC(peso: BigDecimal, tallaCM: BigDecimal): CalculationIndicatorsResult {
        val tallaMetros = tallaCM.divide(BigDecimal(100), 4, RoundingMode.HALF_UP)
        val imc = peso.divide(tallaMetros.pow(2), 2, RoundingMode.HALF_UP)

        return CalculationIndicatorsResult(
            typeIndicatorId = TypeIndicatorIds.IMC,
            value = imc,
            nameIndicator = "IMC",
            status = HealthStatus(0, "")
        )
    }

    private fun calculateSumaPliegues(input: CalculationInput): BigDecimal {
        val pliegues = listOf(
            input.biceps,
            input.triceps,
            input.subescapular,
            input.suprailiaco,
        )

        return pliegues
            .filterNotNull()
            .fold(BigDecimal.ZERO) { total, pliegue -> total.add(pliegue) }
    }

    private fun calculatePorcentajeGrasaCorporal(sumaPliegues: BigDecimal): CalculationIndicatorsResult {
        val log10 = log10(sumaPliegues.toDouble())
        val densidadCorporal = 1.1423 - 0.0632 * log10
        val grasaCorporal = (495 / densidadCorporal) - 450

        return CalculationIndicatorsResult(
            typeIndicatorId = TypeIndicatorIds.PORCENTAJE_GRASA_CORPORAL,
            value = grasaCorporal.toBigDecimal().setScale(2, RoundingMode.HALF_UP),
            nameIndicator = "Porcentaje grasa corporal",
            status = HealthStatus(0, "")
        )
    }

    private fun calculateKgMasaMuscular(sumaPliegues: BigDecimal, peso: BigDecimal): BigDecimal {
        val log10 = log10(sumaPliegues.toDouble())
        val densidadCorporal = 1.1423 - 0.0632 * log10
        val grasaCorporalKg = peso.multiply((4.95 / densidadCorporal - 4.5).toBigDecimal())
        return (peso.subtract(grasaCorporalKg)).setScale(2, RoundingMode.HALF_UP)
    }

    private fun calculatePorcentajeMasaMuscular(
        peso: BigDecimal,
        kgMasaMuscular: BigDecimal
    ): CalculationIndicatorsResult {
        val porcentaje = (kgMasaMuscular.divide(peso, 4, RoundingMode.HALF_UP))
            .multiply(BigDecimal(100))
            .setScale(2, RoundingMode.HALF_UP)

        return CalculationIndicatorsResult(
            typeIndicatorId = TypeIndicatorIds.PORCENTAJE_MASA_MUSCULAR,
            value = porcentaje,
            nameIndicator = "Porcentaje masa muscular",
            status = HealthStatus(0, "")
        )
    }

    private fun calculateGrasaViceral(grasaCorporal: CalculationIndicatorsResult): CalculationIndicatorsResult {
        val grasaVisceral = grasaCorporal.value
            .multiply(BigDecimal("0.90"))
            .setScale(2, RoundingMode.HALF_UP)

        return CalculationIndicatorsResult(
            typeIndicatorId = TypeIndicatorIds.PORCENTAJE_GRASA_VISCERAL,
            nameIndicator = "Porcentaje grasa viceral",
            value = grasaVisceral,
            status = HealthStatus(0, "")
        )
    }

    private fun calculateGrasaViceralFromPercentage(porcentajeGrasa: BigDecimal): CalculationIndicatorsResult {
        val grasaVisceral = porcentajeGrasa
            .multiply(BigDecimal("0.90"))
            .setScale(2, RoundingMode.HALF_UP)

        return CalculationIndicatorsResult(
            typeIndicatorId = TypeIndicatorIds.PORCENTAJE_GRASA_VISCERAL,
            value = grasaVisceral,
            nameIndicator = "Porcentaje grasa visceral",
            status = HealthStatus(0, "")
        )
    }

    private fun calculateIndiceCinturaCadera(
        cintura: BigDecimal,
        cadera: BigDecimal
    ): CalculationIndicatorsResult {
        val indice = cintura.divide(cadera, 2, RoundingMode.HALF_UP)

        return CalculationIndicatorsResult(
            typeIndicatorId = TypeIndicatorIds.INDICE_CINTURA_CADERA,
            value = indice,
            nameIndicator = "Indice cintura cadera",
            status = HealthStatus(0, "")
        )
    }

    private fun calculatePesoLorentz(tallaCm: BigDecimal, genderId: Int): BigDecimal {
        val base = tallaCm.subtract(BigDecimal(100))
        val divisor = if (genderId == 1) BigDecimal("2.5") else BigDecimal(4) // 1=Femenino
        val factor = tallaCm.subtract(BigDecimal(150)).divide(divisor, 2, RoundingMode.HALF_UP)
        return base.subtract(factor).setScale(2, RoundingMode.HALF_UP)
    }

    private fun calculatePesoBrocca(tallaCm: BigDecimal): BigDecimal {
        return tallaCm.subtract(BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
    }

    private fun calculatePesoIdealPromedio(tallaCm: BigDecimal, genderId: Int): BigDecimal {
        val brocca = calculatePesoBrocca(tallaCm)
        val lorentz = calculatePesoLorentz(tallaCm, genderId)
        return brocca.add(lorentz)
            .divide(BigDecimal(2), 2, RoundingMode.HALF_UP)
    }
}