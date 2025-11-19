package domain.interfaces

import application.dto.PatientByIDInfo
import application.dto.UpdatePatientStatusResponse
import domain.models.MedicalRecord
import domain.models.Patient
import domain.models.PatientAggregate
import domain.models.PatientAggregateResponse


interface MedicalRecordInterface {
    suspend fun save(medicalRecord: MedicalRecord): MedicalRecord
    suspend fun findByPatientId(patientId: Int): MedicalRecord?
    suspend fun existsByPatientId(patientId: Int): Boolean
}

interface PatientAggregateInterface {
    suspend fun findById(id: Int, doctorId: Int): PatientByIDInfo?
    suspend fun findByIdPatient(id: Int): Patient?
    suspend fun findAllFiltered(
        doctorId: Int,
        sortBy: String,
        search: String?,
        statusId: Int?,
        limit: Int,
        offset: Int,
    ): List<Patient>
    suspend fun countFiltered(
        doctorId: Int,
        search: String?,
        statusId: Int?
    ): Int
    suspend fun saveCompleteInfo(aggregate: PatientAggregate): PatientAggregateResponse

    suspend fun updateStatus(
        patientId: Int,
        newStatusId: Int,
        doctorId: Int
    ): UpdatePatientStatusResponse
}


