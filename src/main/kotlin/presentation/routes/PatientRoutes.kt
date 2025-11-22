package presentation.routes

import application.dto.AuthDto.ErrorResponse
import application.dto.CreatePatientRequest
import application.dto.PatientByIDInfo
import application.dto.PatientsFilterRequest
import application.dto.UpdatePatientStatusRequest
import application.usecase.PatientUseCase
import application.usecase.GetPatientsFilteredUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.*
import io.ktor.server.routing.get
import io.ktor.server.routing.patch

fun Route.patientRoutes(
    patientUseCase: PatientUseCase,
    getPatientsFilteredUseCase: GetPatientsFilteredUseCase
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

                    val response = patientUseCase.execute(request, doctorId!!)

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

            get {
                try {
                    // 1. Extraer doctorId del JWT
                    val principal = call.principal<JWTPrincipal>()
                    val doctorId = principal?.payload?.getClaim("doctorId")?.asInt()
                        ?: return@get call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Token inválido")
                        )

                    // 2. Obtener query parameters
                    val sortBy = call.request.queryParameters["sortBy"] ?: "recent"
                    val search = call.request.queryParameters["search"]
                    val status = call.request.queryParameters["status"] ?: "active"
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

                    // 3. Crear request
                    val request = PatientsFilterRequest(
                        sortBy = sortBy,
                        search = search,
                        status = status,
                        page = page,
                        limit = limit
                    )

                    // 4. Ejecutar caso de uso
                    val response = getPatientsFilteredUseCase.execute(request, doctorId)

                    call.respond(HttpStatusCode.OK, response)

                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Error al obtener pacientes: ${e.message}")
                    )
                }
            }

            get("/{id}"){
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val doctorId = principal?.payload?.getClaim("doctorId")?.asInt()
                        ?: return@get call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Token inválido")
                        )

                    if (principal == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ErrorResponse(message = "Token inválido")
                        )
                        return@get
                    }

                    val patientIdFromUrl = call.parameters["id"]?.toIntOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(message = "ID de paciente inválido")
                        )

                    val response = patientUseCase.getPatientById(patientIdFromUrl, doctorId)

                    if (response.success) {
                        call.respond(HttpStatusCode.OK, response)
                    } else {
                        call.respond(HttpStatusCode.NotFound, response)
                    }
                }catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        PatientByIDInfo(
                            success = false,
                            message = "Error del servidor: ${e.message}",
                            patient = null
                        )
                    )
                }
            }

            patch("/{id}/status") {
                try {
                    // 1. Extraer doctorId del JWT
                    val principal = call.principal<JWTPrincipal>()
                    val doctorId = principal?.payload?.getClaim("doctorId")?.asInt()
                        ?: return@patch call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Token inválido")
                        )

                    // 2. Extraer patientId de la ruta
                    val patientId = call.parameters["id"]?.toIntOrNull()
                        ?: return@patch call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID de paciente inválido")
                        )

                    // 3. Obtener request body
                    val request = call.receive<UpdatePatientStatusRequest>()

                    // 4. Ejecutar caso de uso
                    val response = patientUseCase.updateStatusPatient(
                        patientId = patientId,
                        request = request,
                        doctorId = doctorId
                    )

                    // 5. Responder según resultado
                    if (response.success) {
                        call.respond(HttpStatusCode.OK, response)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, response)
                    }

                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to e.message)
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Error del servidor: ${e.message}")
                    )
                }
            }

            get("/{id}/stats") {
                try{
                    val principal = call.principal<JWTPrincipal>()
                    val doctorId = principal?.payload?.getClaim("doctorId")?.asInt()
                        ?: return@get call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Token inválido")
                        )

                    val patientIdFromUrl = call.parameters["id"]?.toIntOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(message = "ID de paciente inválido")
                        )

                    val indicator = call.request.queryParameters["indicator"]?.toInt() ?: 1


                    val response = patientUseCase.getPatientStats(patientIdFromUrl, indicator)

                    if (response.success) {
                        call.respond(HttpStatusCode.OK, response)
                    } else {
                        call.respond(HttpStatusCode.NotFound, response)
                    }
                }catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        PatientByIDInfo(
                            success = false,
                            message = "Error del servidor: ${e.message}",
                            patient = null
                        )
                    )
                }
            }

        }
    }
}