package application.usecase.DoctorUseCase

import application.dto.AuthDto.RegisterDoctorRequest
import application.dto.AuthDto.RegisterDoctorResponse
import application.services.PasswordService
import domain.interfaces.DoctorInterface
import domain.models.Doctor
import java.time.LocalDate

class RegisterDoctorUseCase(
    private val doctorInterface: DoctorInterface,
    private val passwordService: PasswordService
) {

    suspend fun execute(request: RegisterDoctorRequest): RegisterDoctorResponse {

        if (doctorInterface.existByEmail(request.email)) {
            return RegisterDoctorResponse(
                success = false,
                message = "El email ya está registrado"
            )
        }

        if (doctorInterface.existsByProfessionalLicense(request.professionalLicense)) {
            return RegisterDoctorResponse(
                success = false,
                message = "La cédula profesional ya está registrado"
            )
        }

        val hashedPassword = passwordService.hashPassword(request.password)

        val doctor = Doctor(
            firstName = request.firstName.trim(),
            lastName = request.lastName.trim(),
            professionalLicense = request.professionalLicense.trim(),
            employmentStart = LocalDate.parse(request.employmentStart),
            graduationInstitution = request.graduationInstitution.trim(),
            currentWorkplace = request.currentWorkplace.trim(),
            email = request.email.trim(),
            password = hashedPassword,
            gender = request.gender
        )

        val savedDoctor = doctorInterface.save(doctor)

        return RegisterDoctorResponse(
            success = true,
            message = "Doctor registrado exitosamente",
            doctorId = savedDoctor.id
        )


    }


}