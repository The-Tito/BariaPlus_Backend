package application.usecase.DoctorUseCase

import application.dto.AuthDto.DoctorInfo
import application.dto.GetReviewsResponse
import application.dto.UpdateDoctorRequest
import application.dto.UpdateDoctorResponse
import application.services.PasswordService
import domain.interfaces.DoctorInterface
import domain.models.Doctor
import domain.models.Review
import java.time.LocalDate

class DoctorUseCase(
    private val doctorInterface: DoctorInterface,
    private val passwordService: PasswordService
){

    suspend fun execute(doctorId: Int, request: UpdateDoctorRequest): UpdateDoctorResponse {

        val existingDoctor = doctorInterface.findById(doctorId)
            ?: return UpdateDoctorResponse(
                success = false,
                message = "Doctor no encontrado",
            )

        if (request.email != null && request.email != existingDoctor.email) {
            if (doctorInterface.existByEmail(request.email)) {
                return UpdateDoctorResponse(
                    success = true,
                    message = "El email ya esta registrado",
                )
            }
        }

        if (request.professionalLicense != null && request.professionalLicense != existingDoctor.professionalLicense) {
            if (doctorInterface.existsByProfessionalLicense(request.professionalLicense)) {
                return UpdateDoctorResponse(
                    success = true,
                    message = "La cédula profesional ya está registrado",
                )
            }
        }

        val hashedPassword = if (request.password != null) {
            passwordService.hashPassword(request.password)
        }else{
            existingDoctor.password
        }

        val updateDoctor = Doctor(
            id = existingDoctor.id,
            firstName = request.firstName?.trim() ?: existingDoctor.firstName,
            lastName = request.lastName?.trim() ?: existingDoctor.lastName,
            professionalLicense = request.professionalLicense?.trim() ?: existingDoctor.professionalLicense,
            employmentStart = request.employmentStart?.let { LocalDate.parse(it) } ?: existingDoctor.employmentStart,
            graduationInstitution = request.graduationInstitution?.trim() ?: existingDoctor.graduationInstitution,
            currentWorkplace = request.currentWorkplace ?: existingDoctor.currentWorkplace,
            email = request.email?.trim() ?: existingDoctor.email,
            password = hashedPassword,
            gender = request.gender ?: existingDoctor.gender,
        )

        val savedDoctor = doctorInterface.update(updateDoctor)

        return UpdateDoctorResponse(
            success = true,
            message = "Doctor actualizado exitosamente",
            doctor = DoctorInfo(
                id = savedDoctor.id!!,
                firstName = savedDoctor.firstName,
                lastName = savedDoctor.lastName,
                email = savedDoctor.email,
                professionalLicenseNumber = savedDoctor.professionalLicense,
                gender = if (savedDoctor.gender == 1) "Femenino" else "Masculino",
                graduationInstitution = savedDoctor.graduationInstitution,
                employmentStart = savedDoctor.employmentStart.toString(),
                currentWorkplace = savedDoctor.currentWorkplace,
            )
        )
    }

    suspend fun getReviewsByDoctorId(doctorId: Int): GetReviewsResponse {

            val response = doctorInterface.getReviewsByDoctorId(doctorId)
        if (!response.success) {
            return GetReviewsResponse(
                success = false,
                message = "No se pudieron obtener las reseñas: ${response.message}",
                average = "0.0",
                reviews = emptyList()
            )
        }

        return response

    }
}