package infrastructure.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object MedicalConsultationsTable : Table("medical_consultations") {
    val id = integer("id").autoIncrement()
    val date = date("date")
    val reason = text("reason")
    val medicalRecordId = integer("medical_record_id")

    override val primaryKey = PrimaryKey(id)
}


object CategoriesTable : Table("categories") {
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

object RangesTable : Table("ranges") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)

    override val primaryKey = PrimaryKey(id)
}

object TypeIndicatorsTable : Table("type_indicators") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val measurementUnitId = integer("measurement_unit_id")
        .references(MeasurementUnitsTable.id)
    val rangeId = integer("range_id").references(RangesTable.id)

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
    val metricCategoryId = integer("metric_category_id")
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

object RangeDetailsTable : Table("range_details") {
    val id = integer("id").autoIncrement()
    val rangeId = integer("range_id").references(RangesTable.id)
    val genderId = integer("gender_id").references(GendersTable.id).nullable()
    val minAge = integer("min_age").nullable()
    val maxAge = integer("max_age").nullable()
    val minValue = decimal("min_value", 5, 2).nullable()
    val maxValue = decimal("max_value", 5, 2).nullable()

    override val primaryKey = PrimaryKey(id)
}