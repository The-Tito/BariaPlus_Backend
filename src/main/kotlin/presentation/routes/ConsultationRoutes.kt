package presentation.routes

import application.dto.AuthDto.ErrorResponse
import application.dto.ConsultationDetailDTO
import application.dto.ConsultationDetailResponse
import application.dto.ConsultationInfoDTO
import application.dto.CreateConsultationRequest
import application.dto.HealthIndicatorsResponseDTO
import application.dto.MetricsValueResponseDTO
import application.dto.NotesResponseDTO
import application.dto.ReviewDTO
import application.usecase.AddReviewUseCase
import application.usecase.CreateConsultationUseCase
import application.usecase.GetCatalogsUseCase
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
    getCatalogsUseCase: GetCatalogsUseCase,
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
                    val response = createConsultationUseCase.execute(request)

                    call.respond(HttpStatusCode.OK, response)

                }catch (e: IllegalArgumentException) {
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

            get("/{id}"){
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
                            medicalRecordId = consultation.consultation.medicalRecordId,
                            notes = consultation.notes.map {
                                NotesResponseDTO(
                                    id = it.id,
                                    description = it.description,
                                    categoryId = it.categoryId,
                                    categoryName = it.categoryName
                                )
                            },
                            healthIndicators = consultation.healthIndicators.map {
                                HealthIndicatorsResponseDTO(
                                    id = it.id,
                                    value = it.value.toString(),
                                    typeIndicatorId = it.typeIndicatorId,
                                    typeIndicatorName = it.typeName,
                                    measurementUnit = it.measurementUnit
                                )
                            },
                            metricsValue = consultation.metricValues.map {
                                MetricsValueResponseDTO(
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
                }catch (e: Exception) {
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