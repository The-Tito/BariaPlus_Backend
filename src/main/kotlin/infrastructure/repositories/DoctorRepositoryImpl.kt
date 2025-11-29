package infrastructure.repositories

import application.dto.GetReviewsResponse
import application.dto.ReviewResponse
import domain.interfaces.DoctorInterface
import domain.models.Doctor
import infrastructure.database.DatabaseFactory.dbQuery
import infrastructure.database.tables.DoctorsTable
import infrastructure.database.tables.DoctorsTable.genderId
import infrastructure.database.tables.MedicalConsultationsTable
import infrastructure.database.tables.MedicalRecordsTable
import infrastructure.database.tables.PatientsTable
import infrastructure.database.tables.ReviewsTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.time.LocalDate

class DoctorRepositoryImpl : DoctorInterface {

    private fun resultRowToDoctor(row: ResultRow) = Doctor(
        id = row[DoctorsTable.id],
        firstName = row[DoctorsTable.first_name],
        lastName = row[DoctorsTable.last_name],
        professionalLicense = row[DoctorsTable.professional_license_number],
        employmentStart = row[DoctorsTable.employment_start_date],
        graduationInstitution = row[DoctorsTable.graduation_institution],
        currentWorkplace = row[DoctorsTable.current_workplace],
        email = row[DoctorsTable.email],
        password = row[DoctorsTable.password_hash],
        gender = row[DoctorsTable.genderId],
    )

    override suspend fun save(doctor: Doctor): Doctor = dbQuery {
        val insertedId = DoctorsTable.insert {
            it[first_name] = doctor.firstName
            it[last_name] = doctor.lastName
            it[professional_license_number] = doctor.professionalLicense
            it[employment_start_date] = doctor.employmentStart
            it[graduation_institution] = doctor.graduationInstitution
            it[current_workplace] = doctor.currentWorkplace
            it[email] = doctor.email
            it[password_hash] = doctor.password
            it[genderId] = doctor.gender
        } get DoctorsTable.id

        doctor.copy(id = insertedId)
    }

    override suspend fun update(doctor: Doctor): Doctor = dbQuery {
        DoctorsTable.update({ DoctorsTable.id eq doctor.id!!}){
            it[first_name] = doctor.firstName
            it[last_name] = doctor.lastName
            it[professional_license_number] = doctor.professionalLicense
            it[employment_start_date] = doctor.employmentStart
            it[graduation_institution] = doctor.graduationInstitution
            it[current_workplace] = doctor.currentWorkplace
            it[email] = doctor.email
            it[password_hash] = doctor.password
            it[genderId] = doctor.gender
        }
        doctor
    }

    override suspend fun findByEmail(email: String): Doctor? = dbQuery {
        DoctorsTable.select { DoctorsTable.email eq email }
            .map { resultRowToDoctor(it) }
            .singleOrNull()
    }

    override suspend fun existByEmail(email: String): Boolean = dbQuery {
        DoctorsTable.select { DoctorsTable.email eq email }
            .count() > 0
    }

    override suspend fun existsByProfessionalLicense(licenseNumber: String): Boolean = dbQuery {
        DoctorsTable
            .select { DoctorsTable.professional_license_number eq licenseNumber }
            .count() > 0
    }

    override suspend fun findById(id: Int): Doctor? = dbQuery {
        DoctorsTable.select { DoctorsTable.id eq id }
            .map { resultRowToDoctor(it) }
            .singleOrNull()
    }

    override suspend fun getReviewsByDoctorId(doctorId: Int): GetReviewsResponse = dbQuery {
        try {
            val last5ReviewsQuery = ReviewsTable
                .join(MedicalConsultationsTable, JoinType.INNER, ReviewsTable.medicalConsultationId, MedicalConsultationsTable.id)
                .join(MedicalRecordsTable, JoinType.INNER, MedicalConsultationsTable.medicalRecordId, MedicalRecordsTable.id)
                .join(PatientsTable, JoinType.INNER, MedicalRecordsTable.patientId, PatientsTable.id)
                .join(DoctorsTable, JoinType.INNER, PatientsTable.doctorId, DoctorsTable.id)
                .select { DoctorsTable.id eq doctorId }
                .orderBy(ReviewsTable.date to SortOrder.DESC)
                .limit(5)

            val last5Reviews = last5ReviewsQuery.map {
                ReviewResponse(
                    id = it[ReviewsTable.id],
                    name = "${it[PatientsTable.firstName]} ${it[PatientsTable.lastName]}",
                    comments = it[ReviewsTable.comments],
                    date = it[ReviewsTable.date].toString(),
                    puntuation = it[ReviewsTable.puntuation]
                )
            }

            val allReviewsQuery = ReviewsTable
                .join(MedicalConsultationsTable, JoinType.INNER, ReviewsTable.medicalConsultationId, MedicalConsultationsTable.id)
                .join(MedicalRecordsTable, JoinType.INNER, MedicalConsultationsTable.medicalRecordId, MedicalRecordsTable.id)
                .join(PatientsTable, JoinType.INNER, MedicalRecordsTable.patientId, PatientsTable.id)
                .join(DoctorsTable, JoinType.INNER, PatientsTable.doctorId, DoctorsTable.id)
                .select { DoctorsTable.id eq doctorId }

            val averagePunctuation = if (allReviewsQuery.any()) {
                allReviewsQuery.map { it[ReviewsTable.puntuation] }.average().toString()
            } else {
                "0.0"
            }

            GetReviewsResponse(
                success = true,
                message = "Reviews obtenidas correctamente",
                average = String.format("%.1f", averagePunctuation.toDoubleOrNull() ?: 0.0),
                reviews = last5Reviews
            )
        } catch (e: Exception) {
            GetReviewsResponse(
                success = false,
                message = "Error al obtener reviews: ${e.message}",
                average = "0.0",
                reviews = emptyList()
            )
        }
    }




    override suspend fun findByProfessionalLicenseNumber(licenseNumber: String): Doctor? = dbQuery {
        DoctorsTable.select { DoctorsTable.professional_license_number eq licenseNumber }
            .map { resultRowToDoctor(it) }
            .singleOrNull()
    }


}