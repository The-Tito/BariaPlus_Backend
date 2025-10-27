package infrastructure.repositories

import domain.interfaces.RangeDetailInterface
import domain.models.RangeDetail
import infrastructure.database.DatabaseFactory.dbQuery
import infrastructure.database.tables.RangeDetailsTable
import infrastructure.database.tables.RangesTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class RangeDetailRepository: RangeDetailInterface {
    override suspend fun findByTypeIndicator(typeIndicatorId: Int): List<RangeDetail> = dbQuery {
        transaction {
            RangeDetailsTable
                .join(
                    RangesTable,
                    JoinType.INNER,
                    additionalConstraint = { RangeDetailsTable.rangeId eq RangesTable.id }
                )
                .select { RangeDetailsTable.typeIndicatorId eq typeIndicatorId }
                .map { row ->
                    RangeDetail(
                        id = row[RangeDetailsTable.id],
                        rangeId = row[RangeDetailsTable.rangeId],
                        rangeName = row[RangesTable.name],
                        genderId = row[RangeDetailsTable.genderId],
                        minAge = row[RangeDetailsTable.minAge],
                        maxAge = row[RangeDetailsTable.maxAge],
                        minValue = row[RangeDetailsTable.minValue],
                        maxValue = row[RangeDetailsTable.maxValue],
                        typeIndicatorId = row[RangeDetailsTable.typeIndicatorId]
                    )
                }
        }
    }

    override suspend fun findById(id: Int): RangeDetail? = dbQuery {
        transaction {
            RangeDetailsTable
                .join(
                    RangesTable,
                    JoinType.INNER,
                    additionalConstraint = { RangeDetailsTable.rangeId eq RangesTable.id }
                )
                .select { RangeDetailsTable.id eq id }
                .map { row ->
                    RangeDetail(
                        id = row[RangeDetailsTable.id],
                        rangeId = row[RangeDetailsTable.rangeId],
                        rangeName = row[RangesTable.name],
                        genderId = row[RangeDetailsTable.genderId],
                        minAge = row[RangeDetailsTable.minAge],
                        maxAge = row[RangeDetailsTable.maxAge],
                        minValue = row[RangeDetailsTable.minValue],
                        maxValue = row[RangeDetailsTable.maxValue],
                        typeIndicatorId = row[RangeDetailsTable.typeIndicatorId]
                    )
                }
                .singleOrNull()
        }
    }
}