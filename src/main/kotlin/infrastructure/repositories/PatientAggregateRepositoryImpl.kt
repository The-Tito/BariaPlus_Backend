package infrastructure.repositories

import domain.interfaces.PatientAggregateInterface
import domain.models.Patient
import domain.models.PatientAggregate
import domain.models.PatientAggregateResponse
import infrastructure.database.DatabaseFactory.dbQuery
import infrastructure.database.tables.AllergiesTable
import infrastructure.database.tables.DiseasesTable
import infrastructure.database.tables.MedicalHistoriesTable
import infrastructure.database.tables.MedicalRecordsTable
import infrastructure.database.tables.PatientsTable
import infrastructure.database.tables.PatientsTable.dateOfBirth
import infrastructure.database.tables.PatientsTable.doctorId
import infrastructure.database.tables.PatientsTable.genderId
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date

class PatientAggregateRepositoryImpl: PatientAggregateInterface {

    private fun ResultRowPatient(row: ResultRow) = Patient(
        id = row[PatientsTable.id],
        firstName = row[PatientsTable.firstName],
        lastName = row[PatientsTable.lastName],
        dateOfBirth = row[PatientsTable.dateOfBirth],
        entryDate = row[PatientsTable.entryDate],
        emergencyNumber = row[PatientsTable.emergencyNumber],
        doctorId = row[PatientsTable.doctorId],
        genderId = row[PatientsTable.genderId],
        statusId = row[PatientsTable.statusId],
    )

    override suspend fun findById(id: Int): Patient? = dbQuery {
        PatientsTable.select { PatientsTable.id eq id }
            .map { ResultRowPatient(it) }
            .singleOrNull()
    }

    override suspend fun saveCompleteInfo(aggregate: PatientAggregate): PatientAggregateResponse = dbQuery {
        transaction {
            try {

                val patientId = PatientsTable.insert {
                    it[firstName] = aggregate.patient.firstName
                    it[lastName] = aggregate.patient.lastName
                    it[dateOfBirth] = aggregate.patient.dateOfBirth
                    it[entryDate] = aggregate.patient.entryDate
                    it[emergencyNumber] = aggregate.patient.emergencyNumber
                    it[doctorId] = aggregate.patient.doctorId
                    it[genderId] = aggregate.patient.genderId
                    it[statusId] = aggregate.patient.statusId
                } get PatientsTable.id

                val savedPatient = aggregate.patient.copy(id = patientId)

                val medicalRecordId = MedicalRecordsTable.insert {
                    it[this.patientId] = patientId
                    it[creationDate] = aggregate.medicalRecord.creationDate
                }get MedicalRecordsTable.id

                val savedMedicalRecord = aggregate.medicalRecord.copy(
                    id = medicalRecordId,
                    patientId = patientId,
                )

                val savedAllergies = aggregate.allergies.map { allergy ->
                    val allergyId = AllergiesTable.insert {
                        it[name] = allergy.name
                        it[allergicReaction] = allergy.allergicReaction
                        it[this.patientId] = patientId
                    } get AllergiesTable.id

                    allergy.copy(id = allergyId, patientId = patientId)
                }

                val savedDiseases = aggregate.diseases.map { disease ->
                    val diseaseId = DiseasesTable.insert {
                        it[name] = disease.name
                        it[actualState] = disease.actualStateId
                        it[this.patientId] = patientId
                    }get DiseasesTable.id

                    disease.copy(id = diseaseId, patientId = patientId)
                }

                val savedHistories = aggregate.medicalHistories.map { history ->
                    val historyId = MedicalHistoriesTable.insert {
                        it[name] = history.name
                        it[detectionDate] = history.detectionDate
                        it[historyTypesId] = history.historyTypeId
                        it[this.patientId] = patientId
                    } get MedicalHistoriesTable.id

                    history.copy(id = historyId, patientId = patientId)
                }




                PatientAggregateResponse(
                    patient = savedPatient,
                    medicalRecord = savedMedicalRecord,
                    allergies = savedAllergies,
                    diseases = savedDiseases,
                    medicalHistories = savedHistories,
                    doctorId = aggregate.patient.doctorId,
                    genderId = aggregate.patient.genderId,
                    dateOfBirth = aggregate.patient.dateOfBirth,

                )
            }catch (e: Exception) {
                // Si algo falla, Exposed hace rollback automático
                throw e
        }
    }
}
}