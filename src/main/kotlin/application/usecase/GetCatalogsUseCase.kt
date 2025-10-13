package application.usecase

import application.dto.CatalogResponseDTO
import application.dto.CategoryDTO
import application.dto.MetricsCatalogDTO
import application.dto.TypeIndicatorDTO
import domain.interfaces.CatalogInterface
import domain.models.MetricsCatalog
import javax.xml.catalog.Catalog

class GetCatalogsUseCase(
    private val catalogInterface: CatalogInterface,
){

    suspend fun execute(): CatalogResponseDTO {

        val categories = catalogInterface.findAllCategories()
        val typeIndicators = catalogInterface.findAllTypeIndicators()
        val metricsCatalog = catalogInterface.findAllMetricsCatalog()

        return CatalogResponseDTO(
            success = true,
            noteCategories = categories.map {
                CategoryDTO(
                    id = it.id,
                    name = it.name,
                )
            },
            typeIndicators = typeIndicators.map {
                TypeIndicatorDTO(
                    id = it.id,
                    name = it.name,
                    measurementUnitId = it.measurementUnitId,
                    measurementUnitName = "" // igual en repo
                )
            },
            metricsCatalog = metricsCatalog.map {
                MetricsCatalogDTO(
                    id = it.id,
                    name = it.name,
                    measurementUnitId = it.measurementUnitId,
                    measurementUnitName = "", // Se llenará en el repositorio
                    categoryId = it.metricCategoryId,
                    categoryName = "" // Se llenará en el repositorio
                )
            }
        )
    }
}