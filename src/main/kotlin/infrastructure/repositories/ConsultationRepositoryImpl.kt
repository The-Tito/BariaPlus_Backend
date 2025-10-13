package infrastructure.repositories

import domain.interfaces.ConsultationInterface
import domain.models.*
import infrastructure.database.DatabaseFactory.dbQuery
import infrastructure.database.tables.*
import org.jetbrains.exposed.sql.*

class ConsultationRepositoryImpl: ConsultationInterface {
    override suspend fun save(consultation: MedicalConsultation): MedicalConsultation = dbQuery {
        val id = MedicalConsultationsTable.insert {
            it[date] = consultation.date
            it[reason] = consultation.reason
            it[medicalRecordId] = consultation.medicalRecordId
        } get MedicalConsultationsTable.id

        consultation.copy(id = id)
    }

    override suspend fun findById(id: Int): MedicalConsultation? = dbQuery {
        MedicalConsultationsTable
            .select { MedicalConsultationsTable.id eq id }
            .map { resultRowToConsultation(it) }
            .singleOrNull()
    }

    override suspend fun findByMedicalRecordId(medicalRecordId: Int): List<MedicalConsultation> = dbQuery {
        MedicalConsultationsTable
            .select { MedicalConsultationsTable.medicalRecordId eq medicalRecordId }
            .map { resultRowToConsultation(it) }
    }

    private fun resultRowToConsultation(row: ResultRow) = MedicalConsultation(
        id = row[MedicalConsultationsTable.id],
        date = row[MedicalConsultationsTable.date],
        reason = row[MedicalConsultationsTable.reason],
        medicalRecordId = row[MedicalConsultationsTable.medicalRecordId],
    )
}