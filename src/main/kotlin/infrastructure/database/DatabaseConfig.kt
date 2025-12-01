package infrastructure.database

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import java.io.File

class DatabaseConfig(
    val dotenv: Dotenv = dotenv {
        ignoreIfMissing = true

        // Detectar ambiente: producción (EC2) o desarrollo (local)
        directory = if (File("/opt/apps/backend/.env").exists()) {
            "/opt/apps/backend"  // Producción (EC2)
        } else {
            "./"  // Desarrollo (raíz del proyecto)
        }
    },

    // SOPORTE DUAL: Intentar formato antiguo (DB_URL) primero,
    // luego construir desde variables individuales
    val jdbcUrl: String = dotenv["DB_URL"] ?: run {
        val host = dotenv["DB_HOST"] ?: "localhost"
        val port = dotenv["DB_PORT"] ?: "5432"
        val database = dotenv["DB_NAME"] ?: error("DB_NAME or DB_URL must be set")
        "jdbc:postgresql://$host:$port/$database"
    },

    // SOPORTE DUAL: DB_USER (antiguo) o DB_USERNAME (nuevo)
    val username: String = dotenv["DB_USER"]
        ?: dotenv["DB_USERNAME"]
        ?: error("DB_USER or DB_USERNAME must be set"),

    val password: String = dotenv["DB_PASSWORD"]
        ?: error("DB_PASSWORD must be set"),

    val driver: String = "org.postgresql.Driver",
    val maxPoolSize: Int = 10,
) {
    init {
        val environment = if (File("/opt/apps/backend/.env").exists()) {
            "PRODUCTION (EC2)"
        } else {
            "DEVELOPMENT (Local)"
        }

        println("==========================================")
        println("🔗 Database Configuration Loaded")
        println("==========================================")
        println("   Environment: $environment")
        println("   JDBC URL: $jdbcUrl")
        println("   Username: $username")
        println("   Password: ${if (password.isNotEmpty()) "***" else "NOT SET"}")
        println("==========================================")
    }
}