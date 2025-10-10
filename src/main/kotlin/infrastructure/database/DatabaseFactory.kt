package infrastructure.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import infrastructure.database.tables.AllergiesTable
import infrastructure.database.tables.DiseasesTable
import infrastructure.database.tables.DoctorsTable
import infrastructure.database.tables.GendersTable
import infrastructure.database.tables.HistoryTypesTable
import infrastructure.database.tables.MedicalHistoriesTable
import infrastructure.database.tables.MedicalRecordsTable
import infrastructure.database.tables.PatientsTable
import infrastructure.database.tables.StatusPatientsTable
import infrastructure.database.tables.StatusRecordsTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


object DatabaseFactory {
    private lateinit var database: Database

    fun init(config: DatabaseConfig = DatabaseConfig()) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.jdbcUrl
            driverClassName = config.driver
            username = config.username
            password = config.password
            maximumPoolSize = config.maxPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            validate()
        }

        val dataSource = HikariDataSource(hikariConfig)
        database = Database.connect(dataSource)


        println("Database connected!")
    }


    /**
     * Ejecutar una query suspendida (async)
     */
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}