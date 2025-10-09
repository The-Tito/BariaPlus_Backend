package application.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import java.util.*

class JWTService{
    private val dotenv = dotenv {
        ignoreIfMissing = true
    }

    private val secret: String = dotenv["SECRET"]
    private val issuer: String = dotenv["ISSUER"]
    private val audience: String = "BariaPlus-API"
    private val expirationTime: Long = 3_600_000 * 24
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(doctorId: Int, email: String): String{
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("doctorId", doctorId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationTime))
            .sign(algorithm)
    }

    fun verifier(): JWTVerifier {
        return JWT
            .require(algorithm)
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    }

    /**
     * Extrae el ID del doctor desde el token
     */
    fun extractDoctorId(token: String): Int? {
        return try {
            val decodedJWT = JWT.decode(token)
            decodedJWT.getClaim("doctorId").asInt()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extrae el email desde el token
     */
    fun extractEmail(token: String): String? {
        return try {
            val decodedJWT = JWT.decode(token)
            decodedJWT.getClaim("email").asString()
        } catch (e: Exception) {
            null
        }
    }
}