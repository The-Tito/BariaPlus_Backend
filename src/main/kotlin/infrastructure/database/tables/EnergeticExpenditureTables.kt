package infrastructure.database.tables

import org.jetbrains.exposed.sql.Table

object EnergeticExpenditureTable : Table("energetic_expenditure") {
    val id = integer("id").autoIncrement()
    val medicalConsultationId = integer("medical_consultation_id")
        .references(MedicalConsultationsTable.id)
    val physicalActivityId = integer("physical_activity_id")
        .references(PhysicalActivityLevelsTable.id)
    val value = decimal("value", 7, 2)
    val reductionPercentage = decimal("reduction_percentage", 5, 2).nullable()
    val adjustedValue = decimal("adjusted_value", 7, 2).nullable()

    override val primaryKey = PrimaryKey(id)
}

object PhysicalActivityLevelsTable : Table("physical_activity_levels") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val description = varchar("description", 255).nullable()
    val activityFactor = decimal("activity_factor", 4, 3)

    override val primaryKey = PrimaryKey(id)
}