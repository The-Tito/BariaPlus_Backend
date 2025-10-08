package infrastructure.database


class DatabaseConfig (
    val jdbcUrl: String = System.getenv("DB_URL")
        ?: "jdbc:postgresql://localhost:5432/bariaplus_bd",
    val username: String = System.getenv("DB_USER")
        ?: "postgres",
    val password: String = System.getenv("DB_PASSWORD")
        ?: "FULL4k1080",
    val driver: String = "org.postgresql.Driver",
    val maxPoolSize: Int = 10,
) {
}