package presentation.routes

import application.dto.AuthDto.ErrorResponse
import application.dto.UpdateDoctorRequest
import application.usecase.DoctorUseCase.DoctorUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.response.respond
import io.ktor.server.routing.get


fun Route.doctorRoutes(
    doctorUseCase: DoctorUseCase,
) {
    authenticate ("auth-jwt") {

            route("/api/doctor") {
                put("/{id}") {
                    try {

                        val principal = call.principal<JWTPrincipal>()
                        val doctorId = principal?.payload?.getClaim("doctorId")?.asInt()
                        if (principal == null) {
                            call.respond(
                                HttpStatusCode.Unauthorized,
                                ErrorResponse(message = "Token inválido")
                            )
                            return@put
                        }

                        val doctorIdFromUrl = call.parameters["id"]?.toIntOrNull()
                            ?: return@put call.respond(
                                HttpStatusCode.BadRequest,
                                ErrorResponse(message = "ID de doctor inválido")
                            )


                        val request = call.receive<UpdateDoctorRequest>()

                        val response = doctorUseCase.execute(doctorIdFromUrl, request)

                        if (response.success) {
                            call.respond(HttpStatusCode.OK, response)
                        } else {
                            call.respond(HttpStatusCode.BadRequest, response.message)
                        }

                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse(message = e.message ?: "Error al actualizar doctor")
                        )
                    }
                }

                get("/{id}/reviews") {
                    val doctorId = call.parameters["id"]?.toIntOrNull()

                    if (doctorId == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(message = "ID de doctor inválido")
                        )
                        return@get
                    }

                    val response = doctorUseCase.getReviewsByDoctorId(doctorId)

                    if (response.success) {
                        call.respond(HttpStatusCode.OK, response)
                    } else {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse(message = response.message)
                        )
                    }
                }

            }
        }
    }