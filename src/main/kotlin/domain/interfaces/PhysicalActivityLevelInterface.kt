package domain.interfaces

import domain.models.PhysicalActivityLevel

interface PhysicalActivityLevelInterface {
    suspend fun findById(id: Int): PhysicalActivityLevel?
}