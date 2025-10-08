package infrastructure.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date


object GendersTable : Table("genders") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 10)

    override val primaryKey = PrimaryKey(id)
}


object DoctorsTable : Table("doctors") {
    val id = integer("id").autoIncrement()
    val first_name = varchar("first_name", 50)
    val last_name = varchar("last_name", 50)
    val professional_license_number = varchar("professional_license_number", 20).uniqueIndex()
    val employment_start_date = date("employment_start_date")
    val graduation_institution = varchar("graduation_institution", 100)
    val current_workplace = varchar("current_workplace", 100)
    val email = varchar("email", 255).uniqueIndex()
    val password_hash = varchar("password_hash", 255)
    val genderId = integer("gender_id").references(GendersTable.id)

    override val primaryKey = PrimaryKey(id)
}