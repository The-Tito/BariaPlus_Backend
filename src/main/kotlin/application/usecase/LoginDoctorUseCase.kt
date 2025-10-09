package application.usecase

import application.dto.AuthDto.DoctorInfo
import application.dto.AuthDto.LoginRequest
import application.dto.AuthDto.LoginResponse
import application.dto.AuthDto.RegisterDoctorRequest
import application.services.JWTService
import application.services.PasswordService
import domain.interfaces.DoctorInterface

class LoginDoctorUseCase(
    private val doctorInterface: DoctorInterface ,
    private val passwordService: PasswordService,
    private val jwtService: JWTService
) {
    suspend fun execute(request: LoginRequest): LoginResponse {
        val doctor = doctorInterface.findByEmail(request.email.trim())
            ?: return LoginResponse(
                success = false,
                message = "Credenciales inválidas"
            )
        val passwordMatches = passwordService.verifyPassword(
            request.password,
            doctor.password
        )

        if (!passwordMatches) {
            return LoginResponse(
                success = false,
                message = "Credenciales inválidas"
            )
        }

        val token = jwtService.generateToken(doctor.id!!, doctor.email)

        val doctorInfo = DoctorInfo(
            id = doctor.id,
            firstName = doctor.firstName,
            lastName = doctor.lastName,
            email = doctor.email,
            professionalLicenseNumber = doctor.professionalLicense
        )

        return LoginResponse(
            success = true,
            message = "Login Exitoso",
            token = token,
            doctor = doctorInfo
        )
    }
}