package application.dto

import application.dto.AuthDto.DoctorInfo
import domain.models.Doctor
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UpdateDoctorRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val professionalLicense: String? = null,
    val employmentStart: String? = null,
    val graduationInstitution: String? = null,
    val currentWorkplace: String? = null,
    val email: String? = null,
    val password: String? = null,
    val gender: Int? = null,
)

@Serializable
data class UpdateDoctorResponse(
    val success: Boolean,
    val message: String,
    val doctor: DoctorInfo? = null,
)