package infrastructure.repositories

import domain.interfaces.DoctorInterface
import domain.models.Doctor
import infrastructure.database.DatabaseFactory.dbQuery
import infrastructure.database.tables.DoctorsTable
import infrastructure.database.tables.DoctorsTable.genderId
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

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

    override suspend fun findByProfessionalLicenseNumber(licenseNumber: String): Doctor? = dbQuery {
        DoctorsTable.select { DoctorsTable.professional_license_number eq licenseNumber }
            .map { resultRowToDoctor(it) }
            .singleOrNull()
    }


}