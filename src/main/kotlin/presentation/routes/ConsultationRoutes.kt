package presentation.routes

import application.dto.AddReviewRequest
import application.dto.AuthDto.ErrorResponse
import application.dto.ConsultationDetailDTO
import application.dto.ConsultationDetailResponse
import application.dto.ConsultationInfoDTO
import application.dto.CreateConsultationRequest
import application.dto.MetricValueDTO
import application.dto.NoteDTO
import application.dto.ReviewDTO
import application.usecase.AddReviewUseCase
import application.usecase.CreateConsultationUseCase
import domain.interfaces.ConsultationAggregateInterface
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.consultationRoutes(
    createConsultationUseCase: CreateConsultationUseCase,
    addReviewUseCase: AddReviewUseCase,
    consultationAggregateInterface: ConsultationAggregateInterface
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

                    val response = ConsultationDetailResponse(
                        success = true,
                        consultation = ConsultationDetailDTO(
                            id = consultation.consultation.id!!,
                            date = consultation.consultation.date.toString(),
                            reason = consultation.consultation.reason,
                            notes = consultation.notes.map {
                                NoteDTO(
                                    id = it.id,
                                    description = it.description,
                                    categoryId = it.categoryId,
                                    categoryName = it.categoryName
                                )
                            },
                            metricValues = consultation.metricValues.map {
                                MetricValueDTO(
                                    id = it.id,
                                    value = it.value.toString(),
                                    metricsCatalogId = it.metricsId,
                                    metricsCatalogName = it.metricName,
                                    measurementUnitName = it.measurementUnit,
                                    metricsCategoryName = it.categoryName
                                )
                            }
                        )
                    )

                    call.respond(HttpStatusCode.OK, response)
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
        }
    }
}