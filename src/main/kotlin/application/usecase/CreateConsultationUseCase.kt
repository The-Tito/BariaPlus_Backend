package application.usecase

import application.dto.CalculatedIndicatorDTO
import application.dto.CalculatedMetricDTO
import application.dto.ConsultationInfoDTO
import application.dto.CreateConsultationRequest
import application.dto.CreateConsultationResponse
import application.dto.MetricValueRequestDTO
import application.dto.NoteRequestDTO
import application.services.CalculationService
import domain.interfaces.ConsultationAggregateInterface
import domain.interfaces.MedicalRecordInterface
import domain.interfaces.PatientAggregateInterface
import domain.models.CalculationCatalog
import domain.models.CalculationInput
import domain.models.ConsultationAggregate
import domain.models.HealthIndicators
import domain.models.MedicalConsultation
import domain.models.MetricCatalogIds
import domain.models.MetricsValue
import domain.models.Notes
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period

class CreateConsultationUseCase(
    private val consultationAggregateInterface: ConsultationAggregateInterface,
    private val patientInterface: PatientAggregateInterface,
    private val medicalRecordInterface: MedicalRecordInterface,
    private val calculationService: CalculationService
) {
    suspend fun execute(request: CreateConsultationRequest, doctorId: Int): CreateConsultationResponse {
        // 1. Validar request
        validateRequest(request)

        // 2. Obtener paciente y verificar pertenencia
        val patient = patientInterface.findById(request.patientId)
            ?: throw IllegalArgumentException("Paciente no encontrado")

        if (patient.doctorId != doctorId) {
            throw IllegalArgumentException("No tienes permiso para crear consultas a este paciente")
        }

        // 3. Verificar expediente médico
        val medicalRecord = medicalRecordInterface.findByPatientId(request.patientId)
            ?: throw IllegalArgumentException("El paciente no tiene expediente médico")

        // 4. Calcular edad del paciente
        val age = Period.between(patient.dateOfBirth, LocalDate.now()).years

        // 5. Construir input para cálculos
        val calculationInput = buildCalculationInput(
            request.metricValues,
            patient.genderId,
            age
        )

        // 6. REALIZAR TODOS LOS CÁLCULOS
        val calculationResult = calculationService.calculateAll(calculationInput)

        // 7. Construir consulta y notas
        val consultation = buildConsultation(request, medicalRecord.id!!)
        val notes = buildNotes(request.notes)

        // 8. Combinar métricas originales + calculadas
        val allMetrics = buildAllMetrics(
            request.metricValues,
            calculationResult.calculatedMetrics
        )

        // 9. Convertir health indicators a entidades de dominio
        val healthIndicators = calculationResult.healthIndicators.map {
            HealthIndicators(
                value = it.value,
                typeIndicatorId = it.typeIndicatorId
            )
        }

        // 10. Crear agregado completo
        val aggregate = ConsultationAggregate(
            consultation = consultation,
            notes = notes,
            metricsValue = allMetrics,
            healthIndicators = healthIndicators
        )

        // 11. Guardar TODO en transacción
        val savedAggregate = consultationAggregateInterface.saveConsultationWithDetails(aggregate)

        // 12. Preparar respuesta (sin estados aún, eso va después)
        return CreateConsultationResponse(
            success = true,
            message = "Consulta creada exitosamente",
            consultation = ConsultationInfoDTO(
                id = savedAggregate.consultation.id!!,
                date = savedAggregate.consultation.date.toString(),
                reason = savedAggregate.consultation.reason,
                medicalRecordId = savedAggregate.consultation.medicalRecordId,
                notesCount = savedAggregate.notes.size,
                healthIndicatorsCount = savedAggregate.healthIndicators.size,
                metricsCount = savedAggregate.metricsValue.size
            ),
            calculatedIndicators = calculationResult.healthIndicators.map {
                CalculatedIndicatorDTO(
                    typeIndicatorId = it.typeIndicatorId,
                    value = it.value.toString(),
                    rangeId = 0, // TODO: Implementar comparación con rangos
                    rangeName = "Pendiente",
                    color = "#9E9E9E"
                )
            },
            calculatedMetrics = calculationResult.calculatedMetrics.map {
                CalculatedMetricDTO(
                    catalogId = it.catalogId,
                    value = it.value.toString()
                )
            }
        )
    }

    /**
     * Construye el input para cálculos extrayendo las métricas del request
     */
    private fun buildCalculationInput(
        metrics: List<MetricValueRequestDTO>,
        genderId: Int,
        age: Int
    ): CalculationInput {
        val metricsMap = metrics.associate { it.metricsCatalogId to BigDecimal(it.value) }

        return CalculationInput(
            peso = metricsMap[MetricCatalogIds.PESO],
            talla = metricsMap[MetricCatalogIds.TALLA],
            cintura = metricsMap[MetricCatalogIds.CINTURA],
            cadera = metricsMap[MetricCatalogIds.CADERA],
            muneca = metricsMap[MetricCatalogIds.MUNECA],
            brazoRelajado = metricsMap[MetricCatalogIds.BRAZO_RELAJADO],
            cuello = metricsMap[MetricCatalogIds.CUELLO],
            muslo = metricsMap[MetricCatalogIds.MUSLO],
            contraido = metricsMap[MetricCatalogIds.CONTRAIDO],
            biceps = metricsMap[MetricCatalogIds.BICEPS],
            triceps = metricsMap[MetricCatalogIds.TRICEPS],
            subescapular = metricsMap[MetricCatalogIds.SUBESCAPULAR],
            ileocrestal = metricsMap[MetricCatalogIds.ILEOCRESTAL],
            suprailiaco = metricsMap[MetricCatalogIds.SUPRAILIACO],
            abdominal = metricsMap[MetricCatalogIds.ABDOMINAL],
            axilaMedial = metricsMap[MetricCatalogIds.AXILA_MEDIAL],
            pectoral = metricsMap[MetricCatalogIds.PECTORAL],
            porcentajeGrasaCorporal = metricsMap[MetricCatalogIds.PORCENTAJE_GRASA_CORPORAL],
            kgMusculo = metricsMap[MetricCatalogIds.KG_MUSCULO],
            kgMasaOsea = metricsMap[MetricCatalogIds.KG_MASA_OSEA],
            porcentajeAguaCorporal = metricsMap[MetricCatalogIds.PORCENTAJE_AGUA],
            ingestionDiariaCal = metricsMap[MetricCatalogIds.INGESTION_DIARIA_CAL],
            edadMetabolica = metricsMap[MetricCatalogIds.EDAD_METABOLICA],
            genderId = genderId,
            age = age
        )
    }

    /**
     * Combina métricas originales del request con las calculadas
     */
    private fun buildAllMetrics(
        originalMetrics: List<MetricValueRequestDTO>,
        calculatedMetrics: List<CalculationCatalog>
    ): List<MetricsValue> {
        val allMetrics = mutableListOf<MetricsValue>()

        // Agregar métricas originales del request
        originalMetrics.forEach {
            allMetrics.add(
                MetricsValue(
                    metricsCatalogId = it.metricsCatalogId,
                    value = BigDecimal(it.value)
                )
            )
        }

        // Agregar métricas calculadas
        calculatedMetrics.forEach {
            allMetrics.add(
                MetricsValue(
                    metricsCatalogId = it.catalogId,
                    value = it.value
                )
            )
        }

        return allMetrics
    }

    private fun buildConsultation(request: CreateConsultationRequest, medicalRecordId: Int) =
        MedicalConsultation(
            date = LocalDate.now(),
            reason = request.reason.trim(),
            medicalRecordId = medicalRecordId
        )

    private fun buildNotes(request: List<NoteRequestDTO>) =
        request.map {
            Notes(
                description = it.description.trim(),
                categoryId = it.categoryId
            )
        }

    private fun validateRequest(request: CreateConsultationRequest) {
        require(request.reason.isNotBlank()) { "La razón de la consulta es requerida" }
        require(request.metricValues.isNotEmpty()) { "Debe proporcionar al menos una métrica" }

        // Validar valores de métricas
        request.metricValues.forEach {
            try {
                val value = BigDecimal(it.value)
                require(value >= BigDecimal.ZERO) { "Los valores no pueden ser negativos" }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Valor de métrica inválido: ${it.value}")
            }
        }
    }
}