package application.dto.AuthDto

import kotlinx.serialization.Serializable
import java.time.LocalDate


@Serializable
data class RegisterDoctorRequest(
    val firstName: String,
    val lastName: String,
    val professionalLicense: String,
    val employmentStart: String,
    val graduationInstitution: String,
    val currentWorkplace: String,
    val email: String,
    val password: String,
    val gender: Int,
)

@Serializable
data class RegisterDoctorResponse(
    val success: Boolean,
    val message: String,
    val doctorId: Int? = null,
)

@Serializable
data class ErrorResponse(
    val success: Boolean = false,
    val message: String,
)