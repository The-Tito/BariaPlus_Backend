package application.dto.AuthDto

import kotlinx.serialization.Serializable


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
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val doctor: DoctorInfo? = null,
)


/**
 * Información básica del doctor en la respuesta
 */
@Serializable
data class DoctorInfo(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val email: String,
    val graduationInstitution: String,
    val employmentStart: String,
    val professionalLicenseNumber: String,
    val currentWorkplace: String
)


@Serializable
data class ErrorResponse(
    val success: Boolean = false,
    val message: String,
)

