package application.dto

import kotlinx.serialization.Serializable

@Serializable
data class PatientsFilterRequest(
    val sortBy: String = "recent",
    val search: String? = null,
    val status: String? = "active",
    val page: Int = 1,
    val limit: Int = 20,
)

@Serializable
data class PatientsListResponse(
    val success: Boolean,
    val patients: List<PatientItemDTO>,
    val pagination: PaginationDTO,
)

@Serializable
data class PatientItemDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val dateOfBirth: String,
    val age: Int,
    val genderId: Int,
    val statusId: Int,
    val entryDate: String,
    val emergencyNumber: String
)

@Serializable
data class PaginationDTO(
    val currentPage: Int,
    val totalPages: Int,
    val totalRecords: Int,
    val recordsPerPage: Int,
)