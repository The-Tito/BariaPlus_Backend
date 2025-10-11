package infrastructure.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

//checar el nombre de esta tabla
object StatusPatientsTable : Table("patient_status") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 10)

    override val primaryKey = PrimaryKey(id)
}
object actualStateTable: Table("actual_state") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 20)
}

/*Pacientes*/
object PatientsTable : Table("patients") {
    val id = integer("id").autoIncrement()
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val dateOfBirth = date("date_of_birth")
    val entryDate = date("entry_date")
    val emergencyNumber = varchar("emergency_number", 20)
    val doctorId = integer("doctor_id").references(DoctorsTable.id)
    val genderId = integer("gender_id").references(GendersTable.id)
    val statusId = integer("status_id").references(StatusPatientsTable.id)

    override val primaryKey = PrimaryKey(id)
}

object AllergiesTable : Table("allergies") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 20)
    val allergicReaction = varchar("allergic_reaction", 255)
    val patientId = integer("patient_id").references(PatientsTable.id)

    override val primaryKey = PrimaryKey(id)
}

object HistoryTypesTable : Table("history_types") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(id)
}

object MedicalHistoriesTable : Table("medical_histories") {
    val id = integer("id").autoIncrement()
    val detectionDate = date("detection_date").nullable()
    val name = varchar("name", 20)
    val patientId = integer("patient_id").references(PatientsTable.id)
    val historyTypesId = integer("history_types_id").references(HistoryTypesTable.id)

    override val primaryKey = PrimaryKey(id)
}

object DiseasesTable : Table("diseases") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val actualState = integer("actual_state_id").references(actualStateTable.id)
    val patientId = integer("patient_id").references(PatientsTable.id)

    override val primaryKey = PrimaryKey(id)
}

object MedicalRecordsTable : Table("medical_records") {
    val id = integer("id").autoIncrement()
    val patientId = integer("patient_id").uniqueIndex().references(PatientsTable.id)
    val creationDate = date("creation_date")

    override val primaryKey = PrimaryKey(id)
}