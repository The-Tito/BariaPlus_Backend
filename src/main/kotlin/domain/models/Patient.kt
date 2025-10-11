package domain.models

import java.time.LocalDate

data class Patient(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: LocalDate,
    val entryDate: LocalDate,
    val emergencyNumber: String,
    val doctorId: Int,
    val genderId: Int,
    val statusId: Int,
)

data class Allergy(
    val id: Int? = null,
    val name: String,
    val allergicReaction: String,
    val patientId: Int? = null,
)

data class Disease(
    val id: Int? = null,
    val name: String,
    val actualStateId: Int,
    val patientId: Int? = null,
)

data class  MedicalHistory(
    val id: Int? = null,
    val detectionDate: LocalDate?,
    val name: String,
    val patientId: Int? = null,
    val historyTypeId: Int,
)

data class MedicalRecord(
    val id: Int? = null,
    val patientId: Int,
    val creationDate: LocalDate,

)
data class PatientAggregate(
    val patient: Patient,
    val medicalRecord: MedicalRecord,
    val allergies: List<Allergy> = emptyList(),
    val diseases: List<Disease> = emptyList(),
    val medicalHistories: List<MedicalHistory> = emptyList()
)