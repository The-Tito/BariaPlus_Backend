package infrastructure.repositories

import domain.interfaces.MedicalRecordInterface
import domain.models.MedicalRecord
import infrastructure.database.DatabaseFactory.dbQuery
import infrastructure.database.tables.MedicalRecordsTable.id
import infrastructure.database.tables.MedicalRecordsTable
import infrastructure.database.tables.MedicalRecordsTable.creationDate
import infrastructure.database.tables.MedicalRecordsTable.patientId
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select


class MedicalRecordRepositoryImpl : MedicalRecordInterface {

    private fun ResultRowMedicalRecord(row: ResultRow) = MedicalRecord(
        id = row[id],
        patientId = row[patientId],
        creationDate = row[creationDate],
    )

    override suspend fun save(medicalRecord: MedicalRecord): MedicalRecord = dbQuery {
        val id = MedicalRecordsTable.insert {
            it[patientId] = medicalRecord.patientId
            it[creationDate] = medicalRecord.creationDate
        }get MedicalRecordsTable.id

        medicalRecord.copy(id = id)
    }

    override suspend fun findByPatientId(patientId: Int): MedicalRecord? = dbQuery {
        MedicalRecordsTable.select { MedicalRecordsTable.patientId eq patientId }
            .map { ResultRowMedicalRecord(it) }
            .singleOrNull()
    }

    override suspend fun existsByPatientId(patientId: Int): Boolean = dbQuery {
        MedicalRecordsTable
            .select { MedicalRecordsTable.patientId eq patientId }
            .count() > 0
    }

}