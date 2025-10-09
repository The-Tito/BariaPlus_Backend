package infrastructure.database

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv


class DatabaseConfig (
    val dotenv: Dotenv = dotenv {
        ignoreIfMissing = true
    },

    val jdbcUrl: String = dotenv["DB_URL"],
    val username: String = dotenv["DB_USER"],
    val password: String = dotenv["DB_PASSWORD"],
    val driver: String = "org.postgresql.Driver",
    val maxPoolSize: Int = 10,
) {

}