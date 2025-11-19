package application.dto

import domain.models.MedicalHistory
import domain.models.MedicalRecord
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class CreatePatientRequest(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val emergencyNumber: String,
    val genderId: Int,
    val statusId: Int = 1, // 1 = Activo por defecto
    val allergies: List<AllergyRequest> = emptyList(),
    val diseases: List<DiseaseRequest> = emptyList(),
    val medicalHistories: List<MedicalHistoryRequest> = emptyList()
)

@Serializable
data class AllergyRequest(
    val name: String,
    val allergicReaction: String,
)

@Serializable
data class DiseaseRequest(
    val name: String,
    val actualStateId: Int,
)

@Serializable
data class MedicalHistoryRequest(
    val name: String,
    val detectionDate: String?,
    val historyTypesId: Int
)

/*Response de un paciente*/

@Serializable
data class CreatePatientResponse(
    val success: Boolean,
    val message: String,
    val patient: PatientInfoDTO? = null
)

@Serializable
data class PatientInfoDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val emergencyNumber: String,
    val genderId: Int,
    val statusId: Int,
    val medicalRecordId: Int,
    val allergiesCount: Int,
    val diseasesCount: Int,
    val medicalHistoriesCount: Int
)

//Get patientByID

@Serializable
data class PatientByIDInfo(
    val success: Boolean,
    val message: String,
    val patient: PatientGetByIDInfo? = null
)

@Serializable
data class PatientGetByIDInfo(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val entryDate: String,
    val emergencyNumber: String,
    val medicalHistories: List<MedicalHistoryResponse> = emptyList(),
    val allergies: List<AllergyResponse> = emptyList(),
    val diseases: List<DiseaseResponse> = emptyList(),
    val consultations: List<ConsultationsResponse> = emptyList()
)

@Serializable
data class AllergyResponse(
    val name: String,
    val allergicReaction: String,
)

@Serializable
data class DiseaseResponse(
    val name: String,
    val actualStateId: Int,
)

@Serializable
data class MedicalHistoryResponse(
    val name: String,
    val historyTypeId: Int,
)

@Serializable
data class ConsultationsResponse(
    val id: Int,
    val consultationDate: String,
)


//Update Patient

@Serializable
data class UpdatePatientStatusRequest(
    val statusId: Int  // 1 = Activo, 2 = Inactivo
)

@Serializable
data class UpdatePatientStatusResponse(
    val success: Boolean,
    val message: String,
    val patient: PatientStatusDTO? = null
)

@Serializable
data class PatientStatusDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val statusId: Int,
    val statusName: String,
    val updatedAt: String  // Timestamp de la actualización
)