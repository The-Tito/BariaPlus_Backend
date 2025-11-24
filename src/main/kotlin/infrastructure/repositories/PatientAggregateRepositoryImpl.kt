package infrastructure.repositories

import application.dto.AllergyResponse
import application.dto.ConsultationsResponse
import application.dto.DiseaseResponse
import application.dto.IndicatorStatsPoint
import application.dto.IndicatorStatsResponse
import application.dto.MedicalHistoryResponse
import application.dto.PatientByIDInfo
import application.dto.PatientGetByIDInfo
import application.dto.PatientStatusDTO
import application.dto.UpdatePatientStatusResponse
import domain.interfaces.PatientAggregateInterface
import domain.models.Patient
import domain.models.PatientAggregate
import domain.models.PatientAggregateResponse
import infrastructure.database.DatabaseFactory.dbQuery
import infrastructure.database.tables.AllergiesTable
import infrastructure.database.tables.DiseasesTable
import infrastructure.database.tables.HealthIndicatorsTable
import infrastructure.database.tables.MedicalConsultationsTable
import infrastructure.database.tables.MedicalHistoriesTable
import infrastructure.database.tables.MedicalRecordsTable
import infrastructure.database.tables.PatientsTable
import infrastructure.database.tables.PatientsTable.dateOfBirth
import infrastructure.database.tables.PatientsTable.genderId
import infrastructure.database.tables.StatusPatientsTable
import infrastructure.database.tables.TypeIndicatorsTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

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

    override suspend fun findById(id: Int, doctorId: Int): PatientByIDInfo? = dbQuery {
        transaction {
            try {
                val patientRow = PatientsTable
                    .select {
                        (PatientsTable.id eq id) and
                                (PatientsTable.doctorId eq doctorId)
                    }
                    .singleOrNull()
                    ?: return@transaction null

                val patientInfo = PatientGetByIDInfo(
                    firstName = patientRow[PatientsTable.firstName],
                    lastName = patientRow[PatientsTable.lastName],
                    dateOfBirth = patientRow[PatientsTable.dateOfBirth].toString(),
                    genderId = patientRow[PatientsTable.genderId],
                    entryDate = patientRow[PatientsTable.entryDate].toString(),
                    emergencyNumber = patientRow[PatientsTable.emergencyNumber],

                    medicalHistories = getMedicalHistoriesByPatientId(id),

                    allergies = getAllergiesByPatientId(id),

                    diseases = getDiseasesByPatientId(id),

                    consultations = getConsultationsByPatientId(id),
                )

                PatientByIDInfo(
                    success = true,
                    message = "Paciente encontrado exitosamente",
                    patient = patientInfo
                )
            }catch (e: Exception) {
                PatientByIDInfo(
                    success = false,
                    message = "Error al obtener paciente: ${e.message}",
                    patient = null
                )
            }
        }

    }

    override suspend fun findByIdPatient(id: Int): Patient? = dbQuery {
        PatientsTable.select { PatientsTable.id eq id }
            .map { ResultRowPatient(it) }.singleOrNull()
    }

    override suspend fun findAllFiltered(
        doctorId: Int,
        sortBy: String,
        search: String?,
        statusId: Int?,
        limit: Int,
        offset: Int
    ): List<Patient> = dbQuery {
        transaction {
            try {
                var query = PatientsTable
                    .select { PatientsTable.doctorId eq doctorId }
                if (!search.isNullOrBlank()) {
                    val searchPattern = "%${search.trim()}%"
                    query = query.andWhere {
                        (PatientsTable.firstName.lowerCase() like searchPattern.lowercase()) or
                                (PatientsTable.lastName.lowerCase() like searchPattern.lowercase())
                    }
                }

                if (statusId != null) {
                    query = query.andWhere { PatientsTable.statusId eq statusId }
                }

                query = when (sortBy) {
                    "recent" -> query.orderBy(PatientsTable.entryDate to SortOrder.DESC)
                    "a-z" -> query.orderBy(PatientsTable.firstName to SortOrder.ASC)
                    "z-a" -> query.orderBy(PatientsTable.firstName to SortOrder.DESC)
                    else -> query.orderBy(PatientsTable.entryDate to SortOrder.DESC)
                }
                query = query.limit(limit, offset.toLong())

                query.map { row ->
                    Patient(
                        id = row[PatientsTable.id],
                        firstName = row[PatientsTable.firstName],
                        lastName = row[PatientsTable.lastName],
                        dateOfBirth = row[dateOfBirth],
                        entryDate = row[PatientsTable.entryDate],
                        emergencyNumber = row[PatientsTable.emergencyNumber],
                        doctorId = row[PatientsTable.doctorId],
                        genderId = row[genderId],
                        statusId = row[PatientsTable.statusId]
                    )
                }

            } catch (e: Exception) {
                throw e
            }
        } as List<Patient>


    }

    override suspend fun countFiltered(
        doctorId: Int,
        search: String?,
        statusId: Int?
    ): Int = dbQuery{
        transaction {
            var query = PatientsTable
                .select { PatientsTable.doctorId eq doctorId }

            if (!search.isNullOrBlank()) {
                val searchPattern = "%${search.trim()}%"
                query = query.andWhere {
                    (PatientsTable.firstName.lowerCase() like searchPattern.lowercase()) or
                            (PatientsTable.lastName.lowerCase() like searchPattern.lowercase())
                }
            }

            if (statusId != null) {
                query = query.andWhere { PatientsTable.statusId eq statusId }
            }

            query.count().toInt()
        }
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
                } get MedicalRecordsTable.id

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
                    } get DiseasesTable.id

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
            } catch (e: Exception) {
                // Si algo falla, Exposed hace rollback automático
                throw e
            }
        }
    }

    override suspend fun updateStatus(
        patientId: Int,
        newStatusId: Int,
        doctorId: Int
    ): UpdatePatientStatusResponse = dbQuery {
        transaction {
            try {

                val patient = PatientsTable
                    .select {
                        (PatientsTable.id eq patientId) and
                                (PatientsTable.doctorId eq doctorId)
                    }
                    .singleOrNull()
                    ?: return@transaction UpdatePatientStatusResponse(
                        success = false,
                        message = "Paciente no encontrado o no tienes permiso para modificarlo",
                        patient = null
                    )


                if (newStatusId !in listOf(1, 2)) {
                    return@transaction UpdatePatientStatusResponse(
                        success = false,
                        message = "Status inválido. Debe ser 1 (Activo) o 2 (Inactivo)",
                        patient = null
                    )
                }


                val updatedRows = PatientsTable.update(
                    where = {
                        (PatientsTable.id eq patientId) and
                                (PatientsTable.doctorId eq doctorId)
                    }
                ) {
                    it[statusId] = newStatusId
                }


                if (updatedRows == 0) {
                    return@transaction UpdatePatientStatusResponse(
                        success = false,
                        message = "No se pudo actualizar el paciente",
                        patient = null
                    )
                }


                val statusName = StatusPatientsTable
                    .select { StatusPatientsTable.id eq newStatusId }
                    .single()[StatusPatientsTable.name]


                UpdatePatientStatusResponse(
                    success = true,
                    message = "Status actualizado exitosamente",
                    patient = PatientStatusDTO(
                        id = patient[PatientsTable.id],
                        firstName = patient[PatientsTable.firstName],
                        lastName = patient[PatientsTable.lastName],
                        statusId = newStatusId,
                        statusName = statusName,
                        updatedAt = LocalDateTime.now().toString()
                    )
                )

            } catch (e: Exception) {
                UpdatePatientStatusResponse(
                    success = false,
                    message = "Error al actualizar status: ${e.message}",
                    patient = null
                )
            }
        }
    }

    override suspend fun getPatientStats(
        patientIdFromUrl: Int,
        indicator: Int
    ): IndicatorStatsResponse = dbQuery {
        // Paso 1: obtener medical_record_id del paciente
        val medicalRecordId = MedicalRecordsTable
            .select { MedicalRecordsTable.patientId eq patientIdFromUrl }
            .singleOrNull()
            ?.get(MedicalRecordsTable.id)
            ?: return@dbQuery IndicatorStatsResponse(
                success = false,
                message = "No existe expediente",
                patientId = patientIdFromUrl,
                indicatorId = indicator,
                indicatorName = "",
                data = emptyList()
            )

        // Paso 2: obtener todas las consultas del paciente (con fechas)
        val consultations = MedicalConsultationsTable
            .select { MedicalConsultationsTable.medicalRecordId eq medicalRecordId }
            .orderBy(MedicalConsultationsTable.date to SortOrder.ASC)
            .map { it[MedicalConsultationsTable.id] to it[MedicalConsultationsTable.date] }

        // Paso 3: para cada consulta, obtener el valor del indicador pedido
        val points = consultations.mapNotNull { (consultationId, date) ->
            val indicatorRow = HealthIndicatorsTable
                .select {
                    (HealthIndicatorsTable.medicalConsultationId eq consultationId) and
                            (HealthIndicatorsTable.typeIndicatorId eq indicator)
                }
                .singleOrNull()

            indicatorRow?.let {
                IndicatorStatsPoint(
                    date = date.toString(),
                    value = it[HealthIndicatorsTable.value].toDouble()
                )
            }
        }

        IndicatorStatsResponse(
            success = true,
            message = "Datos encontrados",
            patientId = patientIdFromUrl,
            indicatorId = indicator,
            indicatorName = getIndicatorName(indicator), // función auxiliar
            data = points
        )
    }

    // Puedes tener una función auxiliar para obtener el nombre del indicador
    private fun getIndicatorName(typeIndicatorId: Int): String {
        return TypeIndicatorsTable
            .select { TypeIndicatorsTable.id eq typeIndicatorId }
            .singleOrNull()
            ?.get(TypeIndicatorsTable.name) ?: "Indicador"
    }
}


    //    Auxiliares
private fun getAllergiesByPatientId(patientId: Int): List<AllergyResponse> {
    return AllergiesTable
        .select { AllergiesTable.patientId eq patientId }
        .map { row ->
            AllergyResponse(
                name = row[AllergiesTable.name],
                allergicReaction = row[AllergiesTable.allergicReaction]
            )
        }
}

    private fun getDiseasesByPatientId(patientId: Int): List<DiseaseResponse> {
        return DiseasesTable
            .select { DiseasesTable.patientId eq patientId }
            .map { row ->
                DiseaseResponse(
                    name = row[DiseasesTable.name],
                    actualStateId = row[DiseasesTable.actualState]
                )
            }
    }

    private fun getMedicalHistoriesByPatientId(patientId: Int): List<MedicalHistoryResponse> {
        return MedicalHistoriesTable
            .select { MedicalHistoriesTable.patientId eq patientId }
            .map { row ->
                MedicalHistoryResponse(
                    name = row[MedicalHistoriesTable.name],
                    historyTypeId = row[MedicalHistoriesTable.historyTypesId]
                )
            }
    }

    private fun getConsultationsByPatientId(patientId: Int): List<ConsultationsResponse> {
        // Primero obtener el medical_record_id del paciente
        val medicalRecordId = MedicalRecordsTable
            .select { MedicalRecordsTable.patientId eq patientId }
            .singleOrNull()
            ?.get(MedicalRecordsTable.id)
            ?: return emptyList()

        // Luego obtener las consultas usando el medical_record_id
        return MedicalConsultationsTable
            .select { MedicalConsultationsTable.medicalRecordId eq medicalRecordId }
            .orderBy(MedicalConsultationsTable.date to SortOrder.DESC)
            .map { row ->
                ConsultationsResponse(
                    id = row[MedicalConsultationsTable.id],
                    consultationDate = row[MedicalConsultationsTable.date].toString()
                )
            }
    }

