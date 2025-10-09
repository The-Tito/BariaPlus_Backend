package presentation.routes.authRoutes

import application.dto.AuthDto.ErrorResponse
import application.dto.AuthDto.LoginRequest
import application.dto.AuthDto.RegisterDoctorRequest
import application.usecase.LoginDoctorUseCase
import application.usecase.RegisterDoctorUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*


fun Route.authRoutes(
    registerDoctorUseCase: RegisterDoctorUseCase,
    loginDoctorUseCase: LoginDoctorUseCase
) {

    route("/api/auth") {

        post("/register") {
            try {
                val request = call.receive<RegisterDoctorRequest>()

                val response = registerDoctorUseCase.execute(request)

                if (response.success) {
                    call.respond(HttpStatusCode.Created, response)
                }else{
                    call.respond(HttpStatusCode.Unauthorized, response)
                }

            }catch (e: Exception) {
                call.application.environment.log.error("Error en registro", e)
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = e.message ?: "Datos invalidos")
                )
            }
        }

        post("/login") {
            try {
                val request = call.receive<LoginRequest>()

                val response = loginDoctorUseCase.execute(request)

                if (response.success) {
                    call.respond(HttpStatusCode.OK, response)
                }else{
                    call.respond(HttpStatusCode.BadRequest, response)
                }
            }catch (e: Exception) {
                call.application.environment.log.error("Error en login", e)
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(message = e.message ?: "Datos invalidos")
                )
            }
        }


    }


}