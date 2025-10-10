package application.dto

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
    val actualState: String,
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
