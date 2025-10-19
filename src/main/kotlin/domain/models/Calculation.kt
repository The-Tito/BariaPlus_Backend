package domain.models

import java.math.BigDecimal

data class CompleteCalculationResult(
    val healthIndicators: List<CalculationIndicatorsResult>,
    val calculatedMetrics: List<CalculationCatalog>,
    val completeInput: CalculationInput
)

data class CalculationIndicatorsResult(
    val typeIndicatorId: Int,
    val value: BigDecimal,
    val status: HealthStatus
)

data class CalculationCatalog(
    val catalogId: Int,
    val value: BigDecimal,
)

data class HealthStatus(
    val rangeId: Int,
    val rangeName: String,
)

data class CalculationInput(
    val peso: BigDecimal? = null,              // ID: 1
    val talla: BigDecimal? = null,             // ID: 2
    val pesoBrocca: BigDecimal? = null,
    val pesoLorentz: BigDecimal? = null,
    val pesoIdealPrimedio: BigDecimal? = null,
    val cintura: BigDecimal? = null,           // ID: 6
    val cadera: BigDecimal? = null,            // ID: 7
    val muneca: BigDecimal? = null,
    val brazoRelajado: BigDecimal? = null,
    val cuello: BigDecimal? = null,
    val muslo: BigDecimal? = null,
    val contraido: BigDecimal? = null,
    val biceps: BigDecimal? = null,
    val triceps: BigDecimal? = null,
    val subescapular: BigDecimal? = null,
    val ileocrestal: BigDecimal? = null,
    val suprailiaco: BigDecimal? = null,
    val abdominal: BigDecimal? = null,
    val axilaMedial: BigDecimal? = null,
    val pectoral: BigDecimal? = null,
    val sumaPliegues: BigDecimal? = null,
    val porcentajeGrasaCorporal: BigDecimal? = null, // ID: 23
    val kgMusculo: BigDecimal? = null,         // ID: 24
    val kgMasaOsea: BigDecimal? = null,
    val porcentajeAguaCorporal: BigDecimal? = null,
    val ingestionDiariaCal: BigDecimal? = null,
    val edadMetabolica: BigDecimal? = null,
    val genderId: Int,
    val age: Int
)


object MetricCatalogIds {
    const val PESO = 1
    const val TALLA = 2
    const val PESO_BROCCA = 3
    const val PESO_LORENTZ = 4
    const val PESO_IDEAL_PROMEDIO = 5
    const val CINTURA = 6
    const val CADERA = 7
    const val MUNECA = 8
    const val BRAZO_RELAJADO = 9
    const val CUELLO = 10
    const val MUSLO = 11
    const val CONTRAIDO = 12
    const val BICEPS = 13
    const val TRICEPS = 14
    const val SUBESCAPULAR = 15
    const val ILEOCRESTAL = 16
    const val SUPRAILIACO = 17
    const val ABDOMINAL = 18
    const val AXILA_MEDIAL = 19
    const val PECTORAL = 20
    const val SUMA_PLIEGUES = 21
    const val PORCENTAJE_GRASA_CORPORAL = 22
    const val KG_MUSCULO = 23
    const val KG_MASA_OSEA = 24
    const val PORCENTAJE_AGUA = 25
    const val INGESTION_DIARIA_CAL = 26
    const val EDAD_METABOLICA = 27
}

object TypeIndicatorIds {
    const val IMC = 1
    const val PORCENTAJE_GRASA_CORPORAL = 2
    const val PORCENTAJE_GRASA_VISCERAL = 3
    const val PORCENTAJE_MASA_MUSCULAR = 4
    const val INDICE_CINTURA_CADERA = 5
}
