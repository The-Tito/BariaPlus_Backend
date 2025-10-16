package infrastructure.repositories

import infrastructure.database.tables.HealthIndicatorsTable
import infrastructure.database.tables.HealthIndicatorsTable.medicalConsultationId
import infrastructure.database.tables.HealthIndicatorsTable.typeIndicatorId
import infrastructure.database.tables.HealthIndicatorsTable.value
import org.jetbrains.exposed.sql.insert

//class HealthIndicatorsRepository {
//
//    val savedHealthIndicators = aggregate.healthIndicators.map { indicator ->
//        val indicatorId = HealthIndicatorsTable.insert {
//            it[value] = indicator.value
//            it[typeIndicatorId] = indicator.typeIndicatorId
//            it[medicalConsultationId] = consultationId
//        } get HealthIndicatorsTable.id
//
//        indicator.copy(id= indicatorId, medicalConsultationId = consultationId)
//    }
//    val healthIndicators = (HealthIndicatorsTable innerJoin TypeIndicatorsTable innerJoin MeasurementUnitsTable)
//        .select { HealthIndicatorsTable.medicalConsultationId eq consultationId }
//        .map {
//            HealthIndicatorWithType(
//                id = it[HealthIndicatorsTable.id],
//                value = it[HealthIndicatorsTable.value],
//                typeIndicatorId = it[HealthIndicatorsTable.typeIndicatorId],
//                typeName = it[TypeIndicatorsTable.name],
//                measurementUnit = it[MeasurementUnitsTable.name]
//            )
//        }
//}