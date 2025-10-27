package infrastructure.database.tables


import org.jetbrains.exposed.sql.Table

object RangesTable : Table("ranges") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)

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
    val typeIndicatorId = integer("type_indicators_id").references(TypeIndicatorsTable.id)

    override val primaryKey = PrimaryKey(id)
}