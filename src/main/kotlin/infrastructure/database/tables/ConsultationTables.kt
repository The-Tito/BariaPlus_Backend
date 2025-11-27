package infrastructure.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.*

object MedicalConsultationsTable : Table("medical_consultations") {
    val id = integer("id").autoIncrement()
    val date = date("date")
    val reason = text("reason")
    val medicalRecordId = integer("medical_records_id")

    override val primaryKey = PrimaryKey(id)
}


object CategoriesTable : Table("note_categories") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(id)
}

object NotesTable : Table("notes") {
    val id = integer("id").autoIncrement()
    val description = text("description")
    val medicalConsultationId = integer("medical_consultation_id")
        .references(MedicalConsultationsTable.id)
    val categoryId = integer("category_id").references(CategoriesTable.id)

    override val primaryKey = PrimaryKey(id)
}

object ReviewsTable : Table("reviews") {
    val id = integer("id").autoIncrement()
    val puntuation = integer("puntuation")
    val comments = text("comments")
    val date = date("date")
    val medicalConsultationId = integer("medical_consultation_id")
        .uniqueIndex()
        .references(MedicalConsultationsTable.id)

    override val primaryKey = PrimaryKey(id)
}

object MeasurementUnitsTable : Table("measurement_units") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 10)

    override val primaryKey = PrimaryKey(id)
}


object TypeIndicatorsTable : Table("type_indicators") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val measurementUnitId = integer("measurement_unit_id")
        .references(MeasurementUnitsTable.id)

    override val primaryKey = PrimaryKey(id)
}

object HealthIndicatorsTable : Table("health_indicators") {
    val id = integer("id").autoIncrement()
    val value = decimal("value", 5, 2)
    val typeIndicatorId = integer("type_indicator_id")
        .references(TypeIndicatorsTable.id)
    val medicalConsultationId = integer("medical_consultation_id")
        .references(MedicalConsultationsTable.id)

    override val primaryKey = PrimaryKey(id)
}

object CategoryMetricTable : Table("category_metric") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 30)

    override val primaryKey = PrimaryKey(id)
}

object MetricsCatalogTable : Table("metrics_catalog") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 30)
    val measurementUnitId = integer("measurement_unit_id")
        .references(MeasurementUnitsTable.id)
    val metricCategoryId = integer("category_metric_id")
        .references(CategoryMetricTable.id)

    override val primaryKey = PrimaryKey(id)
}

object MetricsValueTable : Table("metrics_value") {
    val id = integer("id").autoIncrement()
    val metricsCatalogId = integer("metrics_catalog_id")
        .references(MetricsCatalogTable.id)
    val value = decimal("value", 5, 2)
    val medicalConsultationId = integer("medical_consultation_id")
        .references(MedicalConsultationsTable.id)

    override val primaryKey = PrimaryKey(id)
}

