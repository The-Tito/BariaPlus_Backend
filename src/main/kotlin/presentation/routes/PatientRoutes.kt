package presentation.routes

import application.dto.AuthDto.ErrorResponse
import application.dto.CreatePatientRequest
import application.usecase.CreatePatientUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.*

fun Route.patientRoutes(
    createPatientUseCase: CreatePatientUseCase
) {
    authenticate ("auth-jwt") {

        route("/api/patient") {

            post {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val doctorId = principal?.payload?.getClaim("doctorId")?.asInt()

                    if (principal == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ErrorResponse(message = "Token inválido")
                        )
                        return@post
                    }

                    val request = call.receive<CreatePatientRequest>()

                    val response = createPatientUseCase.execute(request, doctorId!!)

                    call.respond(HttpStatusCode.Created, response)
                }catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(message = e.message ?: "Datos inválidos")
                    )
                } catch (e: Exception) {
                    call.application.environment.log.error("Error al crear paciente", e)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(message = "Error interno del servidor")
                    )
                }
            }
        }
    }
}