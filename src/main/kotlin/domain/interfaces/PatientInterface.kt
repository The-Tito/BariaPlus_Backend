package domain.interfaces

import domain.models.Allergy
import domain.models.Disease
import domain.models.MedicalHistory
import domain.models.MedicalRecord
import domain.models.Patient
import domain.models.PatientAggregate
import domain.models.PatientAggregateResponse

interface AllergyInterface {
    suspend fun saveAll(allergies: List<Allergy>): List<Allergy>
    suspend fun findByPatientId(patientId: Int): List<Allergy>
}

interface DiseaseInterface {
    suspend fun saveAll(diseases: List<Disease>): List<Disease>
    suspend fun findByPatientId(patient: Int): List<Disease>
}

interface MedicalHistoryInterface{
    suspend fun saveAll(medicalHistories: List<MedicalHistory>): List<MedicalHistory>
    suspend fun findByPatientId(patientId: Int): List<MedicalHistory>
}

interface MedicalRecordInterface {
    suspend fun save(medicalRecord: MedicalRecord): MedicalRecord
    suspend fun findByPatientId(patientId: Int): MedicalRecord?
    suspend fun existsByPatientId(patientId: Int): Boolean
}

interface PatientAggregateInterface {
    suspend fun findById(id: Int): Patient?
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
}


