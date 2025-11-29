package domain.interfaces

import application.dto.GetReviewsResponse
import domain.models.Doctor

interface DoctorInterface {
    suspend fun save(doctor: Doctor):Doctor
    suspend fun update(doctor: Doctor):Doctor
    suspend fun findByEmail(email: String): Doctor?
    suspend fun findByProfessionalLicenseNumber(licenseNumber: String): Doctor?
    suspend fun existByEmail(email: String): Boolean
    suspend fun existsByProfessionalLicense(licenseNumber: String): Boolean
    suspend fun findById(id: Int): Doctor?
    suspend fun getReviewsByDoctorId(doctorId: Int): GetReviewsResponse
}