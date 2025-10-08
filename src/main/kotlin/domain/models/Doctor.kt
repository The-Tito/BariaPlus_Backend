package domain.models

import org.jetbrains.exposed.sql.javatime.Date
import java.time.LocalDate

data class Doctor(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val professionalLicense: String,
    val employmentStart: LocalDate,
    val graduationInstitution: String,
    val currentWorkplace: String,
    val email: String,
    val password: String,
    val gender: Int,
    )
