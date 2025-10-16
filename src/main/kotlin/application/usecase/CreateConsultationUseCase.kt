package application.usecase

import application.dto.ConsultationInfoDTO
import application.dto.CreateConsultationRequest
import application.dto.CreateConsultationResponse
import application.dto.MetricsValueRequestDTO
import application.dto.NotesRequestDTO
import domain.interfaces.ConsultationAggregateInterface
import domain.interfaces.MedicalRecordInterface
import domain.models.CalculationInput
import domain.models.ConsultationAggregate
import domain.models.HealthIndicators
import domain.models.MedicalConsultation
import domain.models.MetricCatalogIds
import domain.models.MetricsValue
import domain.models.Notes
import java.math.BigDecimal
import java.time.LocalDate

class CreateConsultationUseCase(
    private val consultationAggregateInterface: ConsultationAggregateInterface,
) {
    suspend fun execute( request: CreateConsultationRequest): CreateConsultationResponse {

        val consultation = buildConsultation(request)
        val notes = buildNotes(request.notes)
        val metricsValues = buildMetricsValue(request.metricValues)

        

        val aggregate = ConsultationAggregate(
            consultation = consultation,
            notes = notes,
            metricsValue = metricsValues,
        )

        val savedAggregate = consultationAggregateInterface.saveConsultationWithDetails(aggregate)

        return CreateConsultationResponse(
            success = true,
            message = "Consulta creada exitosamente",
            consultation = ConsultationInfoDTO(
                id = savedAggregate.consultation.id!!,
                date = savedAggregate.consultation.date.toString(),
                reason = savedAggregate.consultation.reason,
                medicalRecordId = savedAggregate.consultation.medicalRecordId,
                notesCount = savedAggregate.notes.size,
                metricsCount = savedAggregate.metricsValue.size
            )
        )
    }
    private fun buildConsultation(request: CreateConsultationRequest) =
        MedicalConsultation(
            date = LocalDate.now(),
            reason = request.reason.trim(),
            medicalRecordId = request.medicalRecordId,
        )

    private fun buildNotes(request: List<NotesRequestDTO>) =
        request.map {
            Notes(
                description = it.description.trim(),
                categoryId = it.categoryId,
            )
        }

    private fun buildMetricsValue(request: List<MetricsValueRequestDTO>) =
        request.map {
            MetricsValue(
                metricsCatalogId = it.metricsCatalogId,
                value = BigDecimal(it.value),
            )
        }

    private fun buildCalculationInput(
        metrics: List<MetricsValueRequestDTO>,
        genderId: Int,
        age: Int,
    ): CalculationInput {
        val metricsMap = metrics.associate {it.metricsCatalogId to BigDecimal(it.value) }

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
            suprailiaco = metricsMap[MetricCatalogIds.SUPRAILIACO], // Nota: Usamos SUPRAILIACO, asumiendo que es el que se quiere en lugar de ILEOCRESTAL
            abdominal = metricsMap[MetricCatalogIds.ABDOMINAL],
            axilaMedial = metricsMap[MetricCatalogIds.AXILA_MEDIAL],
            pectoral = metricsMap[MetricCatalogIds.PECTORAL],
            genderId =genderId,
            age = age,
        )

    }



}