package domain.interfaces

import domain.models.RangeDetail

interface RangeDetailInterface {
    suspend fun findByTypeIndicator(typeIndicatorId: Int): List<RangeDetail>

    suspend fun findById(id: Int): RangeDetail?
}