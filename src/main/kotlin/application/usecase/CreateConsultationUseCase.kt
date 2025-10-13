package application.usecase

import application.dto.ConsultationInfoDTO
import application.dto.CreateConsultationRequest
import application.dto.CreateConsultationResponse
import application.dto.HealthIndicatorsRequestDTO
import application.dto.MetricsValueRequestDTO
import application.dto.NotesRequestDTO
import domain.interfaces.ConsultationAggregateInterface
import domain.interfaces.MedicalRecordInterface
import domain.models.ConsultationAggregate
import domain.models.HealthIndicators
import domain.models.MedicalConsultation
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
        val healthIndicators = buildHealthIndicators(request.healthIndicators)
        val metricsValues = buildMetricsValue(request.metricValues)
        println(metricsValues.size)

        val aggregate = ConsultationAggregate(
            consultation = consultation,
            notes = notes,
            healthIndicators = healthIndicators,
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
                healthIndicatorsCount = savedAggregate.healthIndicators.size,
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

    private fun buildHealthIndicators(requests: List<HealthIndicatorsRequestDTO>) =
        requests.map {
            HealthIndicators(
                value = BigDecimal(it.value),
                typeIndicatorId = it.typeIndicatorId,
            )
        }

    private fun buildMetricsValue(request: List<MetricsValueRequestDTO>) =
        request.map {
            MetricsValue(
                metricsCatalogId = it.metricsCatalogId,
                value = BigDecimal(it.value),
            )
        }



}