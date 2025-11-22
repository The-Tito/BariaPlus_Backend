package application.usecase

import application.dto.AllergyRequest
import application.dto.CreatePatientRequest
import application.dto.CreatePatientResponse
import application.dto.DiseaseRequest
import application.dto.IndicatorStatsResponse
import application.dto.MedicalHistoryRequest
import application.dto.PatientByIDInfo
import application.dto.PatientInfoDTO
import application.dto.UpdatePatientStatusRequest
import application.dto.UpdatePatientStatusResponse
import domain.interfaces.PatientAggregateInterface
import domain.models.Allergy
import domain.models.Disease
import domain.models.MedicalHistory
import domain.models.MedicalRecord
import domain.models.Patient
import domain.models.PatientAggregate
import java.time.LocalDate


class PatientUseCase(
    private val patientAggregate: PatientAggregateInterface
) {

    suspend fun execute(request: CreatePatientRequest, doctorId: Int): CreatePatientResponse {

        val patient = buildPatient(request, doctorId)
        val medicalRecord = buildMedicalRecord()
        val allergies = buildAllergies(request.allergies)
        val diseases = buildDiseases(request.diseases)
        val medicalHistories = buildMedicalHistories(request.medicalHistories)

        val aggregate = PatientAggregate(
            patient = patient,
            medicalRecord = medicalRecord,
            allergies = allergies,
            diseases = diseases,
            medicalHistories = medicalHistories,
        )

        val savedAggregate = patientAggregate.saveCompleteInfo(aggregate)

        return CreatePatientResponse(
            success = true,
            message = "Paciente creado exitosamente",
            patient = PatientInfoDTO(
                id = savedAggregate.patient.id!!,
                firstName = savedAggregate.patient.firstName,
                lastName = savedAggregate.patient.lastName,
                dateOfBirth = savedAggregate.patient.dateOfBirth.toString(),
                emergencyNumber = savedAggregate.patient.emergencyNumber,
                genderId = savedAggregate.patient.genderId,
                statusId = savedAggregate.patient.statusId,
                medicalRecordId = savedAggregate.medicalRecord.id!!,
                allergiesCount = savedAggregate.allergies.size,
                diseasesCount = savedAggregate.diseases.size,
                medicalHistoriesCount = savedAggregate.medicalHistories.size
            )
        )
    }

    suspend fun getPatientById(id: Int, doctorId: Int): PatientByIDInfo {
        require(id > 0) { "ID de paciente inválido" }

        // Obtener paciente completo
        val result = patientAggregate.findById(id, doctorId)
            ?: return PatientByIDInfo(
                success = false,
                message = "Paciente no encontrado o no tienes permiso",
                patient = null
            )

        return result
    }

    suspend fun updateStatusPatient(
        patientId: Int,
        request: UpdatePatientStatusRequest,
        doctorId: Int
    ): UpdatePatientStatusResponse {

        require(patientId > 0) { "ID de paciente inválido" }

        require(request.statusId in listOf(1, 2)) {
            "Status inválido. Debe ser 1 (Activo) o 2 (Inactivo)"
        }

        return patientAggregate.updateStatus(
            patientId = patientId,
            newStatusId = request.statusId,
            doctorId = doctorId
        )
    }

    suspend fun getPatientStats(patientIdFromUrl: Int, indicator: Int): IndicatorStatsResponse {
        return patientAggregate.getPatientStats(patientIdFromUrl, indicator)
    }

    private fun buildPatient(request: CreatePatientRequest, doctorId: Int) = Patient(
        firstName = request.firstName.trim(),
        lastName = request.lastName.trim(),
        dateOfBirth = LocalDate.parse(request.dateOfBirth.trim()),
        entryDate = LocalDate.now(),
        emergencyNumber = request.emergencyNumber.trim(),
        doctorId = doctorId,
        genderId = request.genderId,
        statusId = request.statusId,
    )

    private fun buildMedicalRecord() = MedicalRecord(
        patientId = 0,
        creationDate = LocalDate.now()
    )

    private fun buildAllergies(requests: List<AllergyRequest>) = requests.map {
        Allergy(
            name = it.name.trim(),
            allergicReaction = it.allergicReaction.trim(),
        )
    }

    private fun buildDiseases(requests: List<DiseaseRequest>) =
        requests.map {
            Disease(
                name = it.name.trim(),
                actualStateId = it.actualStateId
            )
        }

    private fun buildMedicalHistories(requests: List<MedicalHistoryRequest>) =
        requests.map {
            MedicalHistory(
                name = it.name.trim(),
                detectionDate = it.detectionDate?.let { date -> LocalDate.parse(date) },
                historyTypeId = it.historyTypesId
            )
        }



}