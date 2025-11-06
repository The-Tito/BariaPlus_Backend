package application.usecase

import application.dto.PaginationDTO
import application.dto.PatientItemDTO
import application.dto.PatientsFilterRequest
import application.dto.PatientsListResponse
import domain.interfaces.PatientAggregateInterface
import java.time.LocalDate
import java.time.Period

class GetPatientsFilteredUseCase(
    private val patientInterface: PatientAggregateInterface,
) {

    suspend fun execute(
        request: PatientsFilterRequest,
        doctorId: Int
    ): PatientsListResponse {

        // 1. Validar parámetros
        val limit = request.limit.coerceIn(1, 100)  // Máximo 100 registros
        val offset = (request.page - 1) * limit

        // 2. Mapear status string a ID
        val statusId = when (request.status?.lowercase()) {
            "active" -> 1
            "inactive" -> 2
            else -> null  // "all" o null = todos los estados
        }

        // 3. Obtener pacientes filtrados
        val patients = patientInterface.findAllFiltered(
            doctorId = doctorId,
            sortBy = request.sortBy,
            search = request.search,
            statusId = statusId,
            limit = limit,
            offset = offset
        )

        // 4. Obtener total de registros (para paginación)
        val totalRecords = patientInterface.countFiltered(
            doctorId = doctorId,
            search = request.search,
            statusId = statusId
        )

        // 5. Mapear a DTOs
        val patientDTOs = patients.map { patient ->
            val age = Period.between(patient.dateOfBirth, LocalDate.now()).years

            PatientItemDTO(
                id = patient.id!!,
                firstName = patient.firstName,
                lastName = patient.lastName,
                fullName = "${patient.firstName} ${patient.lastName}",
                dateOfBirth = patient.dateOfBirth.toString(),
                age = age,
                genderId = patient.genderId,
                statusId = patient.statusId,
                entryDate = patient.entryDate.toString(),
                emergencyNumber = patient.emergencyNumber
            )
        }

        // 6. Calcular paginación
        val totalPages = (totalRecords + limit - 1) / limit

        return PatientsListResponse(
            success = true,
            patients = patientDTOs,
            pagination = PaginationDTO(
                currentPage = request.page,
                totalPages = totalPages,
                totalRecords = totalRecords,
                recordsPerPage = limit
            )
        )
    }
}