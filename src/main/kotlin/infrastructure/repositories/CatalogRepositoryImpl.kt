package infrastructure.repositories

import domain.interfaces.CatalogInterface
import domain.models.Categories
import domain.models.MetricsCatalog
import domain.models.TypeIndicators
import infrastructure.database.DatabaseFactory.dbQuery
import infrastructure.database.tables.CategoriesTable
import infrastructure.database.tables.MetricsCatalogTable
import infrastructure.database.tables.TypeIndicatorsTable
import jdk.jfr.Category
import org.jetbrains.exposed.sql.selectAll

class CatalogRepositoryImpl : CatalogInterface {

    override suspend fun findAllCategories(): List<Categories> = dbQuery {
        CategoriesTable.selectAll().map {
            Categories(
                id = it[CategoriesTable.id],
                name = it[CategoriesTable.name]
            )
        }
    }

    override suspend fun findAllTypeIndicators(): List<TypeIndicators> = dbQuery {
        TypeIndicatorsTable.selectAll().map {
            TypeIndicators(
                id = it[TypeIndicatorsTable.id],
                name = it[TypeIndicatorsTable.name],
                measurementUnitId = it[TypeIndicatorsTable.measurementUnitId],
                rangeId = it[TypeIndicatorsTable.rangeId]
            )
        }
    }

    override suspend fun findAllMetricsCatalog(): List<MetricsCatalog> = dbQuery {
        MetricsCatalogTable.selectAll().map {
            MetricsCatalog(
                id = it[MetricsCatalogTable.id],
                name = it[MetricsCatalogTable.name],
                measurementUnitId = it[MetricsCatalogTable.measurementUnitId],
                metricCategoryId = it[MetricsCatalogTable.metricCategoryId]
            )
        }
    }
}