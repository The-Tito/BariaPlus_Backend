package infrastructure.repositories

import domain.interfaces.PhysicalActivityLevelInterface
import domain.models.PhysicalActivityLevel
import infrastructure.database.DatabaseFactory.dbQuery
import infrastructure.database.tables.DoctorsTable.id
import infrastructure.database.tables.PhysicalActivityLevelsTable
import infrastructure.database.tables.PhysicalActivityLevelsTable.activityFactor
import infrastructure.database.tables.PhysicalActivityLevelsTable.description
import infrastructure.database.tables.PhysicalActivityLevelsTable.name
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

class PhysicalActivityLevelRepository :  PhysicalActivityLevelInterface{

    private fun resultRowPhysicalActivity(row: ResultRow)= PhysicalActivityLevel(
        id = row[PhysicalActivityLevelsTable.id],
        name = row[PhysicalActivityLevelsTable.name],
        description = row[PhysicalActivityLevelsTable.description],
        activityFactor = row[PhysicalActivityLevelsTable.activityFactor],
    )

    override suspend fun findById(id: Int): PhysicalActivityLevel? = dbQuery {
        PhysicalActivityLevelsTable
            .select { PhysicalActivityLevelsTable.id eq id }
            .map { resultRowPhysicalActivity( it ) }
            .singleOrNull()

    }

}