package presentation.routes

import application.dto.AddReviewRequest
import application.dto.AuthDto.ErrorResponse
import application.dto.ConsultationDetailDTO
import application.dto.ConsultationDetailResponse
import application.dto.ConsultationInfoDTO
import application.dto.CreateConsultationRequest
import application.dto.EnergeticAdjustmentRequestDTO
import application.dto.MetricValueDTO
import application.dto.NoteDTO
import application.dto.ReviewDTO
import application.services.CalculationEnergicService
import application.usecase.AddReviewUseCase
import application.usecase.CreateConsultationUseCase
import domain.interfaces.ConsultationAggregateInterface
import infrastructure.repositories.ConsultationRepositoryImpl
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.consultationRoutes(
    createConsultationUseCase: CreateConsultationUseCase,
    addReviewUseCase: AddReviewUseCase,
    consultationAggregateInterface: ConsultationAggregateInterface,
    calculationEnergicService: CalculationEnergicService
) {

    authenticate("auth-jwt") {
        route("/api/consultations") {

            post {
                try {

                    val principal = call.principal<JWTPrincipal>()!!
                    val doctorId = principal?.payload?.getClaim("doctorId")?.asInt()

                    if (doctorId == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ErrorResponse(message = "Token inválido")
                        )
                        return@post
                    }

                    val request = call.receive<CreateConsultationRequest>()
                    val response = createConsultationUseCase.execute(request, doctorId)

                    call.respond(HttpStatusCode.OK, response)

                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(message = e.message ?: "Datos inválidos")
                    )
                } catch (e: Exception) {
                    call.application.environment.log.error("Error al obtener consulta", e)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(message = "Error interno del servidor")
                    )
                }
            }

            get("/{id}") {
                try {
                    val consultationId = call.parameters["id"]?.toIntOrNull()

                    if (consultationId == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(message = "ID de consulta inválido")
                        )
                        return@get
                    }

                    val consultation = consultationAggregateInterface
                        .findCompleteConsultation(consultationId)

                    if (consultation == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse(message = "Consulta no encontrada")
                        )
                        return@get
                    }


                    if (consultation.success) {
                        call.respond(HttpStatusCode.OK, consultation)
                    } else {
                        call.respond(HttpStatusCode.NotFound, consultation)
                    }
                } catch (e: Exception) {
                    call.application.environment.log.error("Error al obtener consulta", e)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(message = "Error interno del servidor")
                    )
                }
            }
            post("/{id}/review") {
                try {
                    val consultationId = call.parameters["id"]?.toIntOrNull()

                    if (consultationId == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(message = "ID de consulta inválido")
                        )
                        return@post
                    }

                    val request = call.receive<AddReviewRequest>()

                    val response = addReviewUseCase.execute(consultationId, request)
                    if (response.success) {
                        call.respond(HttpStatusCode.OK, response)
                    } else {
                        call.respond(HttpStatusCode.NotFound, response)
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(message = e.message ?: "Datos inválidos")
                    )
                } catch (e: Exception) {
                    call.application.environment.log.error("Error al obtener consulta", e)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(message = "Error interno del servidor")
                    )
                }
            }

            put("/energy-adjustment"){
                val request = call.receive<EnergeticAdjustmentRequestDTO>()

                val doctorId = call.principal<JWTPrincipal>()?.payload?.getClaim("doctorId")?.asInt()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized, "No autorizado")

                val response = calculationEnergicService.applyEnergyAdjustment(request)

                if (response.success) {
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(HttpStatusCode.BadRequest, response)
                }
            }
        }
    }
}